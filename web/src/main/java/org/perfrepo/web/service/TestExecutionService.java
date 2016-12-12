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
    */
   public TestExecution createTestExecution(TestExecution testExecution);

   /**
    * Updates test execution.
    * Update only attributes name, comment, started and collection of tags.
    *
    * @param updatedTestExecution
    * @return
    */
   public TestExecution updateTestExecution(TestExecution updatedTestExecution);

   /**
    * Delete a test execution with all it's subobjects.
    *
    * @param testExecution
    */
   public void removeTestExecution(TestExecution testExecution);

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
   public Long addAttachment(TestExecutionAttachment attachment);

   /**
    * Delete attachment.
    *
    * @param attachment
    */
   public void removeAttachment(TestExecutionAttachment attachment);

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
    */
   public TestExecutionParameter addParameter(TestExecutionParameter parameter);

   /**
    * Updates test execution parameter
    *
    * @param parameter
    * @return test execution parameter
    */
   public TestExecutionParameter updateParameter(TestExecutionParameter parameter);

   /**
    * Removes TestExecutionParameter
    *
    * @param parameter
    */
   public void removeParameter(TestExecutionParameter parameter);

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
    */
   public Value addValue(Value value);

   /**
    * Updates Test Execution Value and the set of it's parameters.
    *
    * @param value
    * @return value
    */
   public Value updateValue(Value value);

   /**
    * Removes value from TestExecution
    *
    * @param value
    */
   public void removeValue(Value value);

   /******** Methods related to tags ********/

   /**
    * Adds tag
    *
    * @param tag
    * @return
    */
   public Tag addTag(Tag tag, TestExecution testExecution);

   /**
    *
    * @param tag
    * @return
    */
   public Tag updateTag(Tag tag);

   /**
    *
    *
    * @param tag
    */
   public void removeTag(Tag tag);

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
