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

import org.apache.log4j.Logger;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TagDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionAttachmentDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.dao.TestExecutionParameterDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.dao.ValueDAO;
import org.perfrepo.web.dao.ValueParameterDAO;
import org.perfrepo.web.security.Secured;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

   private static final Logger log = Logger.getLogger(TestService.class);

   @Inject
   private TestDAO testDAO;

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private TestExecutionParameterDAO testExecutionParameterDAO;

   @Inject
   private TestExecutionAttachmentDAO testExecutionAttachmentDAO;

   @Inject
   private TagDAO tagDAO;

   @Inject
   private ValueDAO valueDAO;

   @Inject
   private ValueParameterDAO valueParameterDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private UserService userService;

   @Inject
   private UserDAO userDAO;

   @Inject
   private AlertingService alertingService;

   @Inject
   private UserSession userSession;

   @Override
   public Test createTest(Test test) throws ServiceException {
      //TODO: solve this
      /*
      if (!userService.isLoggedUserInGroup(test.getGroupId())) {
         throw new org.perfrepo.web.security.SecurityException("securityException.userNotInGroup.createTest", userSession.getLoggedUser().getUsername(), test.getGroupId());
      }*/
      if (testDAO.findByUid(test.getUid()) != null) {
         throw new ServiceException("serviceException.testUidExists", test.getUid());
      }
      Collection<Metric> metrics = test.getMetrics();
      test.setMetrics(null);
      Test createdTest = testDAO.create(test);
      //store metrics
      if (metrics != null) {
         for (Metric metric: metrics) {
            addMetric(metric, test);
         }
      }
      return createdTest;
   }

   @Secured
   @Override
   public Test updateTest(Test test) {
      return testDAO.merge(test);
   }

   @Override
   @Secured
   public void removeTest(Test test) throws ServiceException {
      Test freshTest = testDAO.get(test.getId());

      //TODO: solve this
      /*
      try {
         for (TestExecution testExecution : freshTest.getTestExecutions()) {
            removeTestExecution(testExecution);
         }
      } catch (ServiceException ex) {
         throw new ServiceException("serviceException.removeTest.cannotRemoveAllTestExecutions", ex);
      }*/

      testDAO.remove(freshTest);
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
      return null;
   }

   @Override
   public SearchResultWrapper<Test> searchTest(TestSearchTO search) {
      //TODO: solve this
      //return testDAO.searchTests(search, userService.getLoggedUserGroupNames());
      return null;
   }

   @Override
   public List<String> getTestsByPrefix(String prefix) {
      List<Test> tests = testDAO.findByUIDPrefix(prefix);
      List<String> testuids = new ArrayList<String>();
      //TODO: solve this
      /*
      for (Test test : tests) {
         if (userService.isLoggedUserInGroup(test.getGroupId())) {
            testuids.add(test.getUid());
         }
      }*/
      return testuids;
   }

   @Override
   @Secured
   public Metric addMetric(Metric metric, Test test) throws ServiceException {
      Test freshTest = testDAO.get(test.getId());

      if (metric.getId() != null) {
         // associating an existing metric with the test
         Metric freshMetric = metricDAO.get(metric.getId());
         if (freshMetric == null) {
            throw new ServiceException("serviceException.metricNotFound", metric.getName().toString());
         }

         for (Test testForMetric : freshMetric.getTests()) {
            if (!testForMetric.getGroup().equals(freshTest.getGroup())) {
               throw new ServiceException("serviceException.metricSharingOnlyInGroup");
            }
            if (testForMetric.getId().equals(freshTest.getId())) {
               throw new ServiceException("serviceException.metricAlreadyExists", freshTest.getUid(), freshMetric.getName());
            }
         }

         freshMetric.getTests().add(freshTest);
         freshTest.getMetrics().add(freshMetric);

         freshMetric = metricDAO.merge(freshMetric);
         testDAO.merge(freshTest);

         return freshMetric;
      } else {
         // creating a new metric object
         if (metric.getName() == null) {
            throw new IllegalArgumentException("Metric name is mandatory");
         }
         // metric name needs to be unique in the metric space of a certain groupId
         // does it exist in a test with same group id (including the target test) ?
         List<Metric> existingMetricsForGroup = metricDAO.getMetricByNameAndGroup(metric.getName(),
                                                                                  freshTest.getGroup().getName());
         for (Metric existingMetric : existingMetricsForGroup) {
            if (existingMetric.getName().equals(metric.getName())) {
               Metric freshMetric = metricDAO.get(existingMetric.getId());

               if (freshMetric.getTests().stream()
                       .anyMatch(t -> t.getId().equals(freshTest.getId()))) {
                  throw new ServiceException("serviceException.metricAlreadyExists", freshTest.getUid(), freshMetric.getName());
               }
            }
         }

         //TODO: solve this
         //metric.setTests(Arrays.asList(freshTest));
         Metric freshMetric = metricDAO.create(metric);

         freshTest.getMetrics().add(freshMetric);
         testDAO.merge(freshTest);

         return freshMetric;
      }
   }

   @Override
   @Secured
   public Metric updateMetric(Metric metric) throws ServiceException {
      return metricDAO.merge(metric);
   }

   @Override
   //@Secured TODO: we need to handle this property, since getTestByRelation will not work as metric is associated with more tests
   public void removeMetric(Metric metric) throws ServiceException {
      Metric freshMetric = metricDAO.get(metric.getId());
      //Test freshTest = testDAO.get(test.getId());
      Test freshTest = null;

      //List<Test> newTests = freshMetric.getTests().stream().filter(o -> !o.equals(freshTest)).collect(Collectors.toList());
      //freshMetric.setTests(newTests);

      //List<Metric> newMetrics = freshTest.getMetrics().stream().filter(o -> !o.equals(freshMetric)).collect(Collectors.toList());
      //freshTest.setMetrics(newMetrics);

      freshMetric.getTests().remove(freshTest);
      freshTest.getMetrics().remove(freshMetric);

      metricDAO.merge(freshMetric);
      testDAO.merge(freshTest);

      if (freshMetric.getTests() == null || freshMetric.getTests().isEmpty()) {
         metricDAO.remove(freshMetric);
      }
   }

   @Override
   public Metric getMetric(Long id) {
      Metric metric = metricDAO.get(id);
      if (metric == null) {
         return null;
      }

      if (metric.getTests() != null) {
         List<Test> clonedTests = metric.getTests().stream().collect(Collectors.toList());
         //TODO: solve this
         //metric.setTests(clonedTests);
      }

      return metric;
   }

   @Override
   public List<Metric> getAvailableMetrics(Test test) {
      Test t = testDAO.get(test.getId());

      //TODO: solve this
      //return EntityUtils.removeAllById(metricDAO.getMetricByGroup(t.getGroupId()), t.getMetrics());
      return null;
   }

   @Override
   public List<Metric> getMetricsForTest(Test test) {
      return null;
   }

   @Override
   public void addSubscriber(User user, Test test) {
      Test freshTest = testDAO.get(test.getId());
      User freshUser = userDAO.get(user.getId());

      Collection<User> testSubscribers = freshTest.getSubscribers();
      if (testSubscribers.contains(freshUser)) {
         return;
      }

      testSubscribers.add(freshUser);
      testDAO.merge(freshTest);
   }

   @Override
   public void removeSubscriber(User user, Test test) {
      Test freshTest = testDAO.get(test.getId());

      Collection<User> testSubscribers = new ArrayList<>(freshTest.getSubscribers());
      for (User testSubscriber : freshTest.getSubscribers()) {
         if (testSubscriber.getId().equals(user.getId())) {
            testSubscribers.remove(testSubscriber);
         }
      }

      //TODO: solve this
      //test.setSubscribers(testSubscribers);
      testDAO.merge(test);
   }

   @Override
   public boolean isUserSubscribed(User user, Test test) {
      Test freshTest = testDAO.get(test.getId());
      Collection<User> testSubscribers = freshTest.getSubscribers();
      for (User testSubscriber : testSubscribers) {
         if (testSubscriber.getId().equals(user.getId())) {
            return true;
         }
      }

      return false;
   }

}
