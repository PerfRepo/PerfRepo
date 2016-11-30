/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.service;

import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.security.Secured;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.search.TestSearchCriteria;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements {@link TestService}.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestServiceBean implements TestService {

   @Inject
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private UserService userService;

   @Inject
   private UserDAO userDAO;

   @Inject
   private UserSession userSession;

   @Inject
   private GroupService groupService;

   @Override
   public Test createTest(Test test) throws DuplicateEntityException, UnauthorizedException {
      if (!groupService.isUserInGroup(userSession.getLoggedUser(), test.getGroup())) {
         throw new UnauthorizedException("authorization.cannotCreateTestInGroupIfNotInIt");
      }

      if (getTest(test.getUid()) != null) {
         throw new DuplicateEntityException("test.duplicateUid", test.getUid());
      }

      Set<Metric> metrics = test.getMetrics();
      test.setMetrics(new HashSet<>());
      Test createdTest = testDAO.create(test);

      for (Metric metric: metrics) {
         addMetric(metric, test);
      }

      return createdTest;
   }

   @Secured
   @Override
   public Test updateTest(Test test) throws DuplicateEntityException {
      return testDAO.merge(test);
   }

   @Override
   @Secured
   public void removeTest(Test test) {
      Test managedTest = testDAO.get(test.getId());

      Set<Metric> metrics = getMetricsForTest(managedTest);
      for (Metric metric: metrics) {
         removeMetricFromTest(metric, test);
      }

      // corresponding objects like test executions, alerts etc. will be removed by database cascade
      testDAO.remove(managedTest);
   }

   @Override
   public Test getTest(Long id) {
      return testDAO.get(id);
   }

   @Override
   public Test getTest(String uid) {
      return testDAO.findByUid(uid);
   }

   @Override
   public List<Test> getAllTests() {
      return testDAO.getAll();
   }

   @Override
   public SearchResultWrapper<Test> getTestsForUser(User user) {
      Set<Group> userGroups = userService.getUserGroups(user);
      TestSearchCriteria searchCriteria = new TestSearchCriteria();
      searchCriteria.setGroups(userGroups);

      return searchTests(searchCriteria);
   }

   @Override
   public SearchResultWrapper<Test> searchTests(TestSearchCriteria search) {
      return testDAO.searchTests(search);
   }

   @Override
   public List<Test> getTestsByUidPrefix(String prefix) {
      List<Test> tests = testDAO.findByUIDPrefix(prefix);
      return tests.stream()
              .filter(test -> groupService.isUserInGroup(userSession.getLoggedUser(), test.getGroup()))
              .collect(Collectors.toList());
   }

   @Override
   @Secured
   public Metric addMetric(Metric metric, Test test) {
      Test managedTest = testDAO.get(test.getId());

      Metric managedMetric = metricDAO.getByName(metric.getName());
      if (managedMetric == null) { // creating completely new metric
         managedMetric = metricDAO.create(metric);
      }

      managedMetric.getTests().add(managedTest);
      managedTest.getMetrics().add(managedMetric);

      return managedMetric;
   }

   @Override
   @Secured
   public Metric updateMetric(Metric metric) throws DuplicateEntityException {
      return metricDAO.merge(metric);
   }

   @Override
   //@Secured TODO: we need to handle this property, since getTestByRelation will not work as metric is associated with more tests
   public void removeMetricFromTest(Metric metric, Test test) {
      Metric managedMetric = metricDAO.get(metric.getId());
      Test managedTest = testDAO.get(test.getId());

      managedMetric.getTests().remove(managedTest);
      managedTest.getMetrics().remove(managedMetric);

      if (managedMetric.getTests().isEmpty()) {
         metricDAO.remove(managedMetric);
      }
   }

   @Override
   public Metric getMetric(Long id) {
      return metricDAO.get(id);
   }

   @Override
   public Set<Metric> getMetricsForTest(Test test) {
      Test managedTest = testDAO.get(test.getId());
      return metricDAO.getMetricsByTest(managedTest);
   }

   @Override
   public void addSubscriber(User user, Test test) {
      Test managedTest = testDAO.get(test.getId());
      User managedUser = userDAO.get(user.getId());

      managedTest.getSubscribers().add(managedUser);
      managedUser.getSubscribedTests().add(managedTest);
   }

   @Override
   public void removeSubscriber(User user, Test test) {
      Test managedTest = testDAO.get(test.getId());
      User managedUser = userDAO.get(user.getId());

      managedTest.getSubscribers().remove(managedUser);
      managedUser.getSubscribedTests().remove(test);
   }

   @Override
   public boolean isUserSubscribed(User user, Test test) {
      Test managedTest = testDAO.get(test.getId());
      User managedUser = userDAO.get(user.getId());

      return managedTest.getSubscribers().contains(managedUser);
   }

}
