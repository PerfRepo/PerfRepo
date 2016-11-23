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

import org.perfrepo.model.Tag;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionAttachment;
import org.perfrepo.model.TestExecutionParameter;
import org.perfrepo.model.Value;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * TODO: document this
 * TODO: review comments of all methods
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface TestExecutionService {

   /******** Methods related directly to test execution object ********/

   /**
    * Stores a new test execution.
    *
    * @param testExecution New test execution.
    * @return
    * @throws ServiceException
    */
   public TestExecution createTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Updates test execution.
    * Update only attributes name, comment, started and collection of tags.
    *
    * @param updatedTestExecution
    * @return
    * @throws ServiceException
    */
   public TestExecution updateTestExecution(TestExecution updatedTestExecution) throws ServiceException;

   /**
    * Delete a test execution with all it's subobjects.
    *
    * @param testExecution
    * @throws ServiceException
    */
   public void removeTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Get {@link TestExecution}.
    *
    * @param id
    * @return test execution
    */
   public TestExecution getTestExecution(Long id);

   /**
    * Returns all test executions.
    *
    * @return List of {@link TestExecution}
    */
   public List<TestExecution> getAllTestExecutions();

   /**
    * Returns list of TestExecutions according to criteria defined by TestExecutionSearchTO
    *
    * @param search
    * @return result
    */
   public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchTO search);

   /******** Methods related to test execution attachments ********/

   /**
    * Add attachment to the test execution.
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

   /******** Methods related to test execution parameters ********/

   /**
    * Adds test execution parameter
    *
    * @param parameter
    * @return
    * @throws ServiceException
    */
   public TestExecutionParameter addParameter(TestExecutionParameter parameter) throws ServiceException;

   /**
    * Updates test execution parameter
    *
    * @param parameter
    * @return test execution parameter
    * @throws ServiceException
    */
   public TestExecutionParameter updateParameter(TestExecutionParameter parameter) throws ServiceException;

   /**
    * Removes TestExecutionParameter
    *
    * @param parameter
    * @throws ServiceException
    */
   public void removeParameter(TestExecutionParameter parameter) throws ServiceException;

   /**
    * Get parameter and test execution.
    *
    * @param id
    * @return test execution parameter
    */
   public TestExecutionParameter getParameter(Long id);

   /**
    * Returns test execution parameters matching prefix
    *
    * @param prefix
    * @return
    */
   public List<TestExecutionParameter> getParametersByPrefix(String prefix);

   /******** Methods related to values ********/

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

   /******** Methods related to tags ********/

   /**
    * Returns tags matching prefix
    *
    * @param prefix
    * @return tag prefixes
    */
   public List<String> getTagsByPrefix(String prefix);

   /**
    * Perform mass operation. Adds tags to provided test executions.
    *
    * @param tags
    * @param testExecutions
    */
   public void addTagsToTestExecutions(Set<Tag> tags, Collection<TestExecution> testExecutions);

   /**
    * Perform mass operation. Deletes tags from provided test executions.
    *
    * @param tags
    * @param testExecutions
    */
   public void removeTagsFromTestExecutions(Set<Tag> tags, Collection<TestExecution> testExecutions);

}
