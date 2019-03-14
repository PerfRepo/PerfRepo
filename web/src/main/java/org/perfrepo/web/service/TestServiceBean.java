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
import org.perfrepo.model.*;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.model.util.EntityUtils;
import org.perfrepo.model.util.EntityUtils.UpdateSet;
import org.perfrepo.web.dao.*;
import org.perfrepo.web.security.Secured;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.util.MultiValue;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.*;
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

   @Override
   @Secured
   public TestExecution createTestExecution(TestExecution testExecution) throws ServiceException {
      validateTestExecution(testExecution);

      // The test referred by test execution has to be an existing test
      Test test = testDAO.get(testExecution.getTest().getId());
      testExecution.setTest(test);
      Collection<Tag> detachedTags = testExecution.getTags();
      testExecution.setTags(new HashSet<>());
      TestExecution storedTestExecution = testExecutionDAO.create(testExecution);
      // execution params
      if (testExecution.getParameters() != null && testExecution.getParameters().size() > 0) {
         for (TestExecutionParameter param : testExecution.getParameters()) {
            param.setTestExecution(storedTestExecution);
            testExecutionParameterDAO.create(param);
         }
      }
      // tags
      if (detachedTags != null && detachedTags.size() > 0) {
         for (Tag teg : detachedTags) {
            Tag tag = tagDAO.findByName(teg.getName());
            if (tag == null) {
               tag = tagDAO.create(teg);
            }

            Collection<TestExecution> tagTestExecutions = tag.getTestExecutions();
            if (tagTestExecutions == null) {
               tag.setTestExecutions(new ArrayList<>());
            }

            storedTestExecution.getTags().add(tag);
            tag.getTestExecutions().add(storedTestExecution);

         }
      }
      // values
      if (testExecution.getValues() != null && !testExecution.getValues().isEmpty()) {
         for (Value value : testExecution.getValues()) {
            value.setTestExecution(storedTestExecution);
            if (value.getMetricName() == null) {
               throw new IllegalArgumentException("Metric name is mandatory");
            }
            Metric metric = test.getMetrics().stream().filter(m -> m.getName().equals(value.getMetricName())).findFirst().get();
            if (metric == null) {
               throw new ServiceException("serviceException.metricNotInTest", test.getName(), test.getId().toString(), value.getMetricName());
            }
            value.setMetric(metric);
            valueDAO.create(value);
            if (value.getParameters() != null && value.getParameters().size() > 0) {
               for (ValueParameter vp : value.getParameters()) {
                  vp.setValue(value);
                  valueParameterDAO.create(vp);
               }
            }
         }
      }

      storedTestExecution = testExecutionDAO.update(storedTestExecution);

      TestExecution clone = cloneAndFetch(storedTestExecution, true, true, true, true, true);
      log.debug("Created new test execution " + clone.getId());

      alertingService.processAlerts(clone);

      return clone;
   }

   @Override
   public List<TestExecution> getFullTestExecutions(Collection<Long> ids) {
      List<TestExecution> result = new ArrayList<TestExecution>();
      for (Long id : ids) {
         TestExecution testExecution = getFullTestExecution(id);
         if (testExecution != null) {
            result.add(testExecution);
         }
      }
      return result;
   }

   @Override
   public SearchResultWrapper<Test> searchTest(TestSearchTO search) {
      return testDAO.searchTests(search, userService.getLoggedUserGroupNames());
   }

   @Override
   public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
      // remove param criteria with empty param name
      if (search.getParameters() != null) {
         for (Iterator<ParamCriteria> allParams = search.getParameters().iterator(); allParams.hasNext();) {
            ParamCriteria param = allParams.next();
            if (param.isNameEmpty()) {
               allParams.remove();
            }
         }
      }

      return testExecutionDAO.searchTestExecutions(search, userService.getLoggedUserGroupNames());
   }

   @Override
   public Test getTestByUID(String uid) {
      return testDAO.findByUid(uid);
   }

   @Override
   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs) {
      List<TestExecution> result = new ArrayList<TestExecution>();
      for (String tag : tags) {
         result.addAll(testExecutionDAO.getTestExecutions(Arrays.asList(tag.split(" ")), testUIDs));
      }
      return result;
   }

   @Override
   @Secured
   public Long addAttachment(TestExecutionAttachment attachment) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(attachment.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.addAttachment.testExecutionNotFound", attachment.getTestExecution().getName());
      }
      attachment.setTestExecution(exec);
      TestExecutionAttachment newAttachment = testExecutionAttachmentDAO.create(attachment);
      return newAttachment.getId();
   }

   @Override
   @Secured
   public void removeAttachment(TestExecutionAttachment attachment) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(attachment.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.removeAttachment.testExecutionNotFound", attachment.getTestExecution().getName());
      }
      TestExecutionAttachment freshAttachment = testExecutionAttachmentDAO.get(attachment.getId());
      if (freshAttachment != null) {
         testExecutionAttachmentDAO.remove(freshAttachment);
      }
   }

   @Override
   public TestExecutionAttachment getAttachment(Long id) {
      return testExecutionAttachmentDAO.get(id);
   }

   @Override
   public Test createTest(Test test) throws ServiceException {
      if (!userService.isLoggedUserInGroup(test.getGroupId())) {
         throw new org.perfrepo.web.security.SecurityException("securityException.userNotInGroup.createTest", userService.getLoggedUser().getUsername(), test.getGroupId());
      }
      if (testDAO.findByUid(test.getUid()) != null) {
         throw new ServiceException("serviceException.testUidExists", test.getUid());
      }
      Collection<Metric> metrics = test.getMetrics();
      test.setMetrics(null);
      Test createdTest = testDAO.create(test);
      //store metrics
      if (metrics != null) {
         for (Metric metric: metrics) {
            addMetric(test, metric);
         }
      }
      return createdTest;
   }

   @Override
   public Test getFullTest(Long id) {
      Test test = testDAO.get(id);
      if (test == null) {
         return null;
      }
      test = test.clone();
      // TODO: return by named query, with optimized fetching
      Collection<Metric> metrics = test.getMetrics();
      if (metrics != null) {
         List<Metric> clonedMetrics = new ArrayList<Metric>();
         for (Metric metric: metrics) {
            clonedMetrics.add(metric);
         }
         test.setMetrics(clonedMetrics);
      }

      Collection<User> subscribers = test.getSubscribers();
      if (subscribers != null) {
         List<User> subscribersClone = new ArrayList<>();
         for (User subscriber : subscribers) {
            subscribersClone.add(subscriber.clone());
         }
         test.setSubscribers(subscribersClone);
      }

      Collection<Alert> alerts = test.getAlerts();
      if (alerts != null) {
         List<Alert> alertsClone = new ArrayList<>();
         for (Alert alert : alerts) {
            List<Tag> tagsClone = new ArrayList<>();
            for (Tag tag : alert.getTags()) {
               tagsClone.add(tag.clone());
            }
            Alert alertClone = alert.clone();
            alertClone.setTags(tagsClone);
            alertsClone.add(alertClone);
         }
         test.setAlerts(alertsClone);
      }

      return test;
   }

   @Override
   public List<Test> getAllFullTests() {
      List<Test> r = testDAO.getAll();
      List<Test> rcopy = new ArrayList<Test>(r.size());
      for (Test t : r) {
         rcopy.add(getFullTest(t.getId()));
      }
      return rcopy;
   }

   @Secured
   @Override
   public Test updateTest(Test test) {
      return testDAO.update(test);
   }

   @Override
   @Secured
   public void removeTest(Test test) throws ServiceException {
      Test freshTest = testDAO.get(test.getId());
      try {
         for (TestExecution testExecution : freshTest.getTestExecutions()) {
            removeTestExecution(testExecution);
         }
      } catch (ServiceException ex) {
         throw new ServiceException("serviceException.removeTest.cannotRemoveAllTestExecutions", ex);
      }

      testDAO.remove(freshTest);
   }

   @Override
   @Secured
   public void removeTestExecution(TestExecution testExecution) throws ServiceException {
      TestExecution freshTestExecution = testExecutionDAO.get(testExecution.getId());
      if (freshTestExecution == null) {
         throw new ServiceException("serviceException.testExecutionNotFound", testExecution.getName());
      }
      for (TestExecutionParameter testExecutionParameter : freshTestExecution.getParameters()) {
         testExecutionParameterDAO.remove(testExecutionParameter);
      }
      for (Value value : freshTestExecution.getValues()) {
         for (ValueParameter valueParameter : value.getParameters()) {
            valueParameterDAO.remove(valueParameter);
         }
         valueDAO.remove(value);
      }

      Iterator<TestExecutionAttachment> allTestExecutionAttachments = freshTestExecution.getAttachments().iterator();
      while (allTestExecutionAttachments.hasNext()) {
         testExecutionAttachmentDAO.remove(allTestExecutionAttachments.next());
         allTestExecutionAttachments.remove();
      }
      testExecutionDAO.remove(freshTestExecution);
   }

   @Override
   public List<Metric> getAvailableMetrics(Test test) {
      Test t = testDAO.get(test.getId());
      return EntityUtils.removeAllById(metricDAO.getMetricByGroup(t.getGroupId()), t.getMetrics());
   }

   @Override
   @Secured
   public Metric addMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = testDAO.get(test.getId());
      if (freshTest.getMetrics() == null) {
         freshTest.setMetrics(new ArrayList<>());
      }

      if (metric.getId() != null) {
         // associating an existing metric with the test
         Metric freshMetric = metricDAO.get(metric.getId());
         if (freshMetric == null) {
            throw new ServiceException("serviceException.metricNotFound", metric.getName().toString());
         }

         if (freshMetric.getTests() == null) {
            freshMetric.setTests(new ArrayList<>());
         }

         for (Test testForMetric : freshMetric.getTests()) {
            if (!testForMetric.getGroupId().equals(freshTest.getGroupId())) {
               throw new ServiceException("serviceException.metricSharingOnlyInGroup");
            }
            if (testForMetric.getId().equals(freshTest.getId())) {
               throw new ServiceException("serviceException.metricAlreadyExists", freshTest.getUid(), freshMetric.getName());
            }
         }

         freshMetric.getTests().add(freshTest);
         freshTest.getMetrics().add(freshMetric);

         freshMetric = metricDAO.update(freshMetric);
         testDAO.update(freshTest);

         return freshMetric;
      } else {
         // creating a new metric object
         if (metric.getName() == null) {
            throw new IllegalArgumentException("Metric name is mandatory");
         }
         // metric name needs to be unique in the metric space of a certain groupId
         // does it exist in a test with same group id (including the target test) ?
         List<Metric> existingMetricsForGroup = metricDAO.getMetricByNameAndGroup(metric.getName(),
                                                                                  freshTest.getGroupId());
         for (Metric existingMetric : existingMetricsForGroup) {
            if (existingMetric.getName().equals(metric.getName())) {
               Metric freshMetric = metricDAO.get(existingMetric.getId());

               if (freshMetric.getTests().stream()
                       .anyMatch(t -> t.getId().equals(freshTest.getId()))) {
                  throw new ServiceException("serviceException.metricAlreadyExists", freshTest.getUid(), freshMetric.getName());
               }
            }
         }

         metric.setTests(Arrays.asList(freshTest));
         Metric freshMetric = metricDAO.create(metric);

         freshTest.getMetrics().add(freshMetric);
         testDAO.update(freshTest);

         return freshMetric;
      }
   }

   @Override
   @Secured
   public Metric updateMetric(Metric metric) throws ServiceException {
      return metricDAO.update(metric);
   }

   @Override
   public List<Metric> getTestMetrics(Test test) {
      Test t = testDAO.get(test.getId());
      return t.getSortedMetrics();
   }

   @Override
   //@Secured TODO: we need to handle this property, since getTestByRelation will not work as metric is associated with more tests
   public void removeMetric(Metric metric, Test test) throws ServiceException {
      Metric freshMetric = metricDAO.get(metric.getId());
      Test freshTest = testDAO.get(test.getId());

      //List<Test> newTests = freshMetric.getTests().stream().filter(o -> !o.equals(freshTest)).collect(Collectors.toList());
      //freshMetric.setTests(newTests);

      //List<Metric> newMetrics = freshTest.getMetrics().stream().filter(o -> !o.equals(freshMetric)).collect(Collectors.toList());
      //freshTest.setMetrics(newMetrics);

      freshMetric.getTests().remove(freshTest);
      freshTest.getMetrics().remove(freshMetric);

      metricDAO.update(freshMetric);
      testDAO.update(freshTest);

      if (freshMetric.getTests() == null || freshMetric.getTests().isEmpty()) {
         metricDAO.remove(freshMetric);
      }
   }

   @Override
   public Metric getFullMetric(Long id) {
      Metric metric = metricDAO.get(id);
      if (metric == null) {
         return null;
      }
      metric = metric.clone();

      if (metric.getTests() != null) {
         List<Test> clonedTests = metric.getTests().stream().collect(Collectors.toList());
         metric.setTests(clonedTests);
      }

      return metric;
   }

   @Override
   public TestExecution getFullTestExecution(Long id) {
      return cloneAndFetch(testExecutionDAO.get(id), true, true, true, true, true);
   }

   @Override
   public List<TestExecution> getAllFullTestExecutions() {
      List<TestExecution> r = testExecutionDAO.getAll();
      List<TestExecution> rcopy = new ArrayList<TestExecution>(r.size());
      for (TestExecution exec : r) {
         rcopy.add(getFullTestExecution(exec.getId()));
      }
      return rcopy;
   }

   @Override
   public List<TestExecution> getExecutionsByTest(Long testId) {
      return testExecutionDAO.getByTest(testId);
   }

   @Override
   @Secured
   public TestExecution updateTestExecution(TestExecution updatedTestExecution) throws ServiceException {
      validateTestExecution(updatedTestExecution);

      TestExecution freshTestExecution = testExecutionDAO.get(updatedTestExecution.getId());
      if (freshTestExecution == null) {
         throw new ServiceException("serviceException.testExecutionNotFound", updatedTestExecution.getName());
      }

      freshTestExecution.setName(updatedTestExecution.getName());
      freshTestExecution.setStarted(updatedTestExecution.getStarted());
      freshTestExecution.setComment(updatedTestExecution.getComment());

      testExecutionDAO.update(freshTestExecution);

      updateTags(freshTestExecution, updatedTestExecution);
      updateValues(freshTestExecution, updatedTestExecution);
      updateParameters(freshTestExecution, updatedTestExecution);

      testExecutionDAO.update(freshTestExecution);

      TestExecution execClone = cloneAndFetch(freshTestExecution, true, true, true, true, true);
      return execClone;
   }

   @Override
   @Secured
   public TestExecutionParameter updateParameter(TestExecutionParameter tep) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(tep.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.testExecutionNotFound", tep.getTestExecution().getName());
      }
      if (testExecutionParameterDAO.hasTestParam(exec.getId(), tep)) {
         throw new ServiceException("serviceException.parameterExists", tep.getName());
      }

      return testExecutionParameterDAO.update(tep);
   }

   @Override
   public TestExecutionParameter getFullParameter(Long paramId) {
      TestExecutionParameter p = testExecutionParameterDAO.get(paramId);
      if (p == null) {
         return null;
      }

      TestExecutionParameter pclone = p.clone();
      pclone.setTestExecution(p.getTestExecution().clone());

      return pclone;
   }

   @Override
   @Secured
   public void removeParameter(TestExecutionParameter tep) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(tep.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.testExecutionNotFound", tep.getTestExecution().getName());
      }
      TestExecutionParameter tepRemove = testExecutionParameterDAO.get(tep.getId());
      testExecutionParameterDAO.remove(tepRemove);
   }

   @Override
   @Secured
   public Value addValue(Value value) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(value.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.addValue.testExecutionNotFound", value.getTestExecution().getName());
      }
      Metric metric = null;
      if (value.getMetric().getId() != null) {
         metric = metricDAO.get(value.getMetric().getId());
      } else {
         List<Metric> metrics = metricDAO.getMetricByNameAndGroup(value.getMetric().getName(), exec.getTest()
             .getGroupId());
         if (metrics.size() > 0) {
            metric = metricDAO.get(metrics.get(0).getId());
         }
      }
      if (metric == null) {
         throw new ServiceException("serviceException.metricNotFound", value.getMetric().getName());
      }
      value.setTestExecution(exec);
      value.setMetric(metric);
      // check if other values for given metric exist, if yes, we can only add one if both old and new one have at least one parameter
      List<Value> existingValuesForMetric = valueDAO.find(exec.getId(), metric.getId());
      if (!existingValuesForMetric.isEmpty()) {
         for (Value v : existingValuesForMetric) {
            if (!v.hasParameters()) {
               throw new ServiceException("serviceException.unparametrizedMultiValue");
            }
         }
         if (!value.hasParameters()) {
            throw new ServiceException("serviceException.unparametrizedMultiValue");
         }
      }
      Value freshValue = valueDAO.create(value);
      Value freshValueClone = freshValue.clone();
      List<ValueParameter> newParams = new ArrayList<ValueParameter>();
      if (value.hasParameters()) {
         for (ValueParameter valueParameter : value.getParameters()) {
            valueParameter.setValue(freshValue);
            valueParameter.setId(null);
            newParams.add(valueParameterDAO.create(valueParameter).clone());
            newParams.get(newParams.size() - 1).setValue(freshValueClone);
         }
      }
      freshValueClone.setParameters(newParams.isEmpty() ? null : newParams);
      return freshValueClone;
   }

   @Override
   @Secured
   public Value updateValue(Value value) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(value.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.updateValue.testExecutionNotFound", value.getTestExecution().getName());
      }
      Value oldValue = valueDAO.get(value.getId());
      if (oldValue == null) {
         throw new ServiceException("serviceException.valueNotFound");
      }
      Value freshValue = valueDAO.update(value);
      Value freshValueClone = freshValue.clone();
      freshValueClone.setMetric(freshValue.getMetric().clone());
      freshValueClone.getMetric().setValues(null);
      UpdateSet<ValueParameter> updateSet = EntityUtils.updateSet(oldValue.getParameters(), value.getParameters());
      if (!updateSet.removed.isEmpty()) {
         throw new ServiceException("serviceException.staleCollection");
      }
      List<ValueParameter> newParams = new ArrayList<ValueParameter>();
      for (ValueParameter vp : updateSet.toAdd) {
         vp.setValue(freshValue);
         newParams.add(valueParameterDAO.create(vp).clone());
         newParams.get(newParams.size() - 1).setValue(freshValueClone);
      }
      for (ValueParameter vp : updateSet.toUpdate) {
         newParams.add(valueParameterDAO.update(vp).clone());
         newParams.get(newParams.size() - 1).setValue(freshValueClone);
      }
      for (ValueParameter vp : updateSet.toRemove) {
         valueParameterDAO.remove(vp);
      }
      freshValueClone.setParameters(newParams.isEmpty() ? null : newParams);
      return freshValueClone;
   }

   @Override
   @Secured
   public void removeValue(Value value) throws ServiceException {
      TestExecution exec = testExecutionDAO.get(value.getTestExecution().getId());
      if (exec == null) {
         throw new ServiceException("serviceException.removeValue.testExecutionNotFound", value.getTestExecution().getName());
      }
      Value v = valueDAO.get(value.getId());
      for (ValueParameter vp : v.getParameters()) {
         valueParameterDAO.remove(vp);
      }
      valueDAO.remove(v);
   }

   @Override
   public List<Test> getAllTests() {
      return testDAO.getAll();
   }

   @Override
   public SearchResultWrapper<Test> getAvailableTests() {
      TestSearchTO search = new TestSearchTO();
      search.setGroupFilter(GroupFilter.MY_GROUPS);
      List<String> groups = userService.getLoggedUserGroupNames();

      return testDAO.searchTests(search, groups);
   }

   @Override
   public List<Metric> getAllMetrics(Long testId) {
      Test freshTest = testDAO.get(testId);
      if (freshTest.getMetrics() == null) {
         return new ArrayList<>();
      }

      List<Metric> metrics = freshTest.getMetrics().stream().collect(Collectors.toList());
      return metrics;
   }

   @Override
   public List<String> getTestsByPrefix(String prefix) {
      List<Test> tests = testDAO.findByUIDPrefix(prefix);
      List<String> testuids = new ArrayList<String>();
      for (Test test : tests) {
         if (userService.isLoggedUserInGroup(test.getGroupId())) {
            testuids.add(test.getUid());
         }
      }
      return testuids;
   }

   @Override
   public List<String> getParametersByPrefix(String prefix) {
      List<TestExecutionParameter> parameters = testExecutionParameterDAO.findByPrefix(prefix);
      List<String> result = new ArrayList<>();
      for (TestExecutionParameter parameter : parameters) {
         result.add(parameter.getName());
      }
      return result;
   }

   @Override
   public List<String> getTagsByPrefix(String prefix) {
      List<String> tags = new ArrayList<String>();
      for (Tag tag : tagDAO.findByPrefix(prefix)) {
         tags.add(tag.getName());
      }
      return tags;
   }

   @Override
   public void addTagsToTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions) {
      for (TestExecution testExecutionItem : testExecutions) {
         TestExecution testExecution = testExecutionDAO.get(testExecutionItem.getId());
         if (testExecution == null) {
            continue;
         }

         for (String tagName : tags) {
            if (!testExecution.getTags().contains(tagName)) {
               Tag tag = tagDAO.findByName(tagName);
               if (tag == null) {
                  Tag newTag = new Tag();
                  newTag.setName(tagName);
                  tag = tagDAO.create(newTag);
               }

               Collection<TestExecution> tagTestExecutions = tag.getTestExecutions();
               if (tagTestExecutions == null) {
                  tag.setTestExecutions(new ArrayList<>());
               }

               tag.getTestExecutions().add(testExecution);
               testExecution.getTags().add(tag);
            }
         }

         testExecutionDAO.update(testExecution);
      }
   }

   @Override
   public void removeTagsFromTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions) {
      for (TestExecution testExecutionItem : testExecutions) {
         TestExecution testExecution = testExecutionDAO.get(testExecutionItem.getId());
         if (testExecution == null) {
            continue;
         }

         List<Tag> tagList = new ArrayList<>(testExecution.getTags());
         for (Tag tag : tagList) {
            if (tags.contains(tag.getName())) {
               testExecution.getTags().remove(tag);
            }
         }

         testExecutionDAO.update(testExecution);
      }
   }

   @Override
   public Test getTest(Long id) {
      return testDAO.get(id);
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
      testDAO.update(freshTest);
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

      test.setSubscribers(testSubscribers);
      testDAO.update(test);
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

   private TestExecution cloneAndFetch(TestExecution exec, boolean fetchTest, boolean fetchParameters,
                                       boolean fetchTags, boolean fetchValues,
                                       boolean fetchAttachments) {
      if (exec == null) {
         return null;
      }
      TestExecution clone = exec.clone();
      if (fetchTest) {
         TestExecutionDAO.fetchTest(clone);
      } else {
         clone.setTest(null);
      }
      if (fetchParameters) {
         TestExecutionDAO.fetchParameters(clone);
      } else {
         clone.setParameters(null);
      }
      if (fetchTags) {
         TestExecutionDAO.fetchTags(clone);
      } else {
         clone.setTags(null);
      }
      if (fetchValues) {
         TestExecutionDAO.fetchValues(clone);
      } else {
         clone.setValues(null);
      }
      if (fetchAttachments) {
         TestExecutionDAO.fetchAttachments(clone);
      } else {
         clone.setAttachments(null);
      }
      return clone;
   }

   /**
    * Validates correct format of test execution.
    *
    * @param testExecution
    * @throws ServiceException
     */
   private void validateTestExecution(TestExecution testExecution) throws ServiceException {
      try {
         boolean isMultivalue = MultiValue.isMultivalue(testExecution);
      } catch (IllegalStateException ex) {
         log.error(ex.getMessage());
         throw new ServiceException("page.exec.invalidMultiValue", testExecution.getName());
      }
   }

   /**
    * Helper method for updating values of existing test execution.
    *
    * @param freshTestExecution
    * @param updatedTestExecution
    * @throws ServiceException
    */
   private void updateValues(TestExecution freshTestExecution, TestExecution updatedTestExecution) throws ServiceException {
      freshTestExecution.getValues().stream().forEach(valueDAO::remove);
      freshTestExecution.setValues(new ArrayList<>());

      for (Value value: updatedTestExecution.getValues()) {
         value.setTestExecution(freshTestExecution);
         value.setId(null);
         addValue(value);
      }
   }

   /**
    * Helper method for updating parameters of existing test execution.
    *
    * @param freshTestExecution
    * @param updatedTestExecution
    * @throws ServiceException
    */
   private void updateParameters(TestExecution freshTestExecution, TestExecution updatedTestExecution) throws ServiceException {
      freshTestExecution.getParameters().stream().forEach(testExecutionParameterDAO::remove);
      freshTestExecution.setParameters(new ArrayList<>());

      for (TestExecutionParameter parameter: updatedTestExecution.getParameters()) {
         parameter.setTestExecution(freshTestExecution);
         parameter.setId(null);
         updateParameter(parameter);
      }
   }

   /**
    * Helper method for updating tags of existing test execution.
    *
    * @param freshTestExecution
    * @param updatedTestExecution
    * @throws ServiceException
    */
   private void updateTags(TestExecution freshTestExecution, TestExecution updatedTestExecution) {
      freshTestExecution.setTags(new ArrayList<>());

      for (Tag tag : updatedTestExecution.getTags()) {
         Tag tagEntity = tagDAO.findByName(tag.getName());
         if (tagEntity == null) {
            Tag newTag = new Tag();
            newTag.setName(tag.getName());
            tagEntity = tagDAO.create(newTag);
         }

         Collection<TestExecution> tagTestExecutions = tagEntity.getTestExecutions();
         if (tagTestExecutions == null) {
            tagEntity.setTestExecutions(new ArrayList<>());
         }

         tagEntity.getTestExecutions().add(freshTestExecution);
         freshTestExecution.getTags().add(tagEntity);
      }
   }
}
