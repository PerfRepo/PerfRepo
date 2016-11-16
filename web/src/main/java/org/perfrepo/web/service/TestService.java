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

import org.perfrepo.model.*;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.List;

/**
 * Main facade to the test execution services.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public interface TestService {

   /**
    * Stores a new test execution.
    * <p/>
    * The new test execution needs to contain test UID and the referred test has to exist.
    *
    * @param testExecution New test execution.
    * @return Created test execution. Contains database IDs.
    * @throws org.perfrepo.web.service.exceptions.ServiceException
    */
   public TestExecution createTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Returns list of TestExecutions according to criteria defined by TestExecutionSearchTO
    *
    * @param search
    * @return result
    */
   public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchTO search);

   /**
    * Returns list of Tests according to criteria defined by TestSearchTO
    *
    * @param search
    * @return result
    */
   public SearchResultWrapper<Test> searchTest(TestSearchTO search);

   /**
    * Get list of full test executions.
    *
    * @param ids
    * @return List of {@link TestExecution}
    */
   public List<TestExecution> getFullTestExecutions(Collection<Long> ids);

   /**
    * Get metric with all associated tests (without details).
    *
    * @param id
    * @return metric
    */
   public Metric getFullMetric(Long id);

   /**
    * Add attachment to the test execution. The {@link TestExecution} object referred by attachment
    * needs to be an empty object with only id set.
    *
    * @param attachment
    * @return id of newly created attachment
    */
   public Long addAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Delete attachment.
    *
    * @param attachment
    * @throws ServiceException
    */
   public void removeAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Get test execution attachment by id.
    *
    * @param id
    * @return attachment
    */
   public TestExecutionAttachment getAttachment(Long id);

   /**
    * Create a new test with collection of metrics.Group id of the new test needs to be one
    * of the current user's roles.
    *
    * @param test
    * @return newly created test
    * @throws ServiceException
    */
   public Test createTest(Test test) throws ServiceException;

   /**
    * Delete a test execution with all it's subobjects.
    *
    * @param testExecution
    * @throws ServiceException
    */
   public void removeTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Get {@link TestExecution} with all details.
    *
    * @param id
    * @return test execution
    */
   public TestExecution getFullTestExecution(Long id);

   /**
    * Returns all test executions with all information.
    *
    * @return List of {@link TestExecution}
    */
   public List<TestExecution> getAllFullTestExecutions();

   /**
    * Returns all test executions of the specified test.
    *
    * @param testId
    * @return List of {@link TestExecution}
    */
   public List<TestExecution> getExecutionsByTest(Long testId);

   /**
    * Returns TestExecutions by tags and test uids
    *
    * @param tags
    * @param testUIDs
    * @return test executions
    */
   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs);

   /**
    * Add metric to given test.
    *
    * @param test
    * @param metric
    * @return metric
    * @throws ServiceException
    */
   public Metric addMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Update metric.
    *
    * @param metric
    * @return Updated metric
    * @throws ServiceException
    */
   public Metric updateMetric(Metric metric) throws ServiceException;

   /**
    * Get test with all metrics but without executions.
    *
    * @param testId Test id
    * @return Test
    */
   public Test getFullTest(Long testId);

   /**
    * Delete a test with all it's sub-objects. Use with caution!
    *
    * @param test
    * @throws ServiceException
    */
   public void removeTest(Test test) throws ServiceException;

   /**
    * Updates the test.
    *
    * @param test
    * @return test
    */
   public Test updateTest(Test test);

   /**
    * Returns all tests with all information.
    *
    * @return tests
    */
   public List<Test> getAllFullTests();

   /**
    * Returns metrics which belong tests with defined group id and are not defined on the defined
    * test
    *
    * @param test
    * @return metrics
    */
   public List<Metric> getAvailableMetrics(Test test);

   /**
    * Returns all metrics, which are defined on the Test
    *
    * @return metrics
    */
   public List<Metric> getTestMetrics(Test test);

   /**
    * Delete metric from the test.
    *
    * @param metric
    * @param test
    * @throws ServiceException
    */
   public void removeMetric(Metric metric, Test test) throws ServiceException;

   /**
    * Update only attributes name and started and collection of tags.
    *
    * @param updatedTestExecution
    * @return fresh full test execution
    * @throws ServiceException
    */
   public TestExecution updateTestExecution(TestExecution updatedTestExecution) throws ServiceException;

   /**
    * Get parameter and test execution.
    *
    * @param paramId
    * @return test execution parameter
    */
   public TestExecutionParameter getFullParameter(Long paramId);

   /**
    * Updates or creates TestExecutionParameter
    *
    * @param tep TestExecutionParameter to update
    * @return test execution parameter
    * @throws ServiceException
    */
   public TestExecutionParameter updateParameter(TestExecutionParameter tep) throws ServiceException;

   /**
    * Removes TestExecutionParameter
    *
    * @param tep
    * @throws ServiceException
    */
   public void removeParameter(TestExecutionParameter tep) throws ServiceException;

   /**
    * Creates new value.
    *
    * @param value
    * @return value
    * @throws ServiceException
    */
   public Value addValue(Value value) throws ServiceException;

   /**
    * Updates Test Execution Value and the set of it's parameters.
    *
    * @param value
    * @return value
    * @throws ServiceException
    */
   public Value updateValue(Value value) throws ServiceException;

   /**
    * Removes value from TestExecution
    *
    * @param value
    * @throws ServiceException
    */
   public void removeValue(Value value) throws ServiceException;

   /**
    * @return lists of tests for selection in UI.
    */
   public List<Test> getAllTests();

   /**
    * @return list of all test belonging to one of the groups current logged user is in.
    */
   public SearchResultWrapper<Test> getAvailableTests();

   /**
    * @param testId
    * @return All metrics for given testId
    */
   public List<Metric> getAllMetrics(Long testId);

   /**
    * Returns test uids matching prefix
    *
    * @param prefix
    * @return test prefixes
    */
   public List<String> getTestsByPrefix(String prefix);

   /**
    * Returns test execution parameters names matching prefix
    *
    * @param prefix
    * @return
    */
   public List<String> getParametersByPrefix(String prefix);

   /**
    * Returns tags matching prefix
    *
    * @param prefix
    * @return tag prefixes
    */
   public List<String> getTagsByPrefix(String prefix);

   /**
    * Returns test by test uid
    *
    * @param uid
    * @return test
    */
   public Test getTestByUID(String uid);

   /**
    * Perform mass operation. Adds tags to provided test executions.
    *
    * @param tags
    * @param testExecutions
    */
   public void addTagsToTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions);

   /**
    * Perform mass operation. Deletes tags from provided test executions.
    *
    * @param tags
    * @param testExecutions
    */
   public void removeTagsFromTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions);

   /**
    * Retrieves test by id
    *
    * @param id
    * @return test
    */
   public Test getTest(Long id);

   /**
    * Adds given user to the subscriber list of the given test.
    *
    * @param user
    * @param test
    * @throws ServiceException if subscriber already exists
    */
   public void addSubscriber(User user, Test test);

   /**
    * Removes given user from the subscriber list of the given test
    *
    * @param user
    * @param test
    */
   public void removeSubscriber(User user, Test test);

   /**
    * Returns true if the given user is subscribed to given test
    *
    * @param user
    * @param test
    * @return boolean
    */
   public boolean isUserSubscribed(User user, Test test);

}
