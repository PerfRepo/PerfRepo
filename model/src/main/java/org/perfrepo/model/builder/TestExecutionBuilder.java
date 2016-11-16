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
package org.perfrepo.model.builder;

import org.perfrepo.model.*;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * {@link TestExecution} builder.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class TestExecutionBuilder {
   private TestExecution testExecution;

   /**
    * Create a new {@link TestExecutionBuilder}.
    */
   public TestExecutionBuilder() {
      this.testExecution = new TestExecution();
   }

   /**
    * Sets the id of the {@link TestExecution}
    *
    * @param id
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder id(Long id) {
      testExecution.setId(id);
      return this;
   }

   /**
    * Set the name.
    *
    * @param name
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder name(String name) {
      testExecution.setName(name);
      return this;
   }

   /**
    * Set the started date.
    *
    * @param started
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder started(Date started) {
      testExecution.setStarted(started);
      return this;
   }

   /**
    * Set execution comment.
    *
    * @param comment
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder comment(String comment) {
      testExecution.setComment(comment);
      return this;
   }

   private TestExecutionParameter addParameter(TestExecutionParameter param) {
      Map<String, TestExecutionParameter> parameters = testExecution.getParameters();
      TestExecutionParameter existingParameter = findParameter(param.getName());
      if (existingParameter != null) {
         existingParameter.setValue(param.getValue());
      } else {
         parameters.put(param.getName(), param);
      }
      return param;
   }

   private TestExecutionParameter findParameter(String name) {
      if (testExecution == null || testExecution.getParameters() == null || name == null) {
         return null;
      }

      return testExecution.getParameters().get(name);
   }

   private TestExecutionParameter addParameter(String name, String value) {
      return addParameter(new TestExecutionParameter(name, value));
   }

   /**
    * Add a new {@link TestExecutionParameter} to this {@link TestExecution} with given name and
    * value
    *
    * @param name
    * @param value
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder parameter(String name, String value) {
      addParameter(name, value);
      return this;
   }

   private Tag addTag(String tag) {
      Set<Tag> tags = testExecution.getTags();

      Tag newTag = new Tag();
      newTag.setName(tag);
      tags.add(newTag);

      return newTag;
   }

   /**
    * Add a new {@link Tag} to this {@link TestExecution} with given name
    *
    * @param tag
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder tag(String tag) {
      addTag(tag);
      return this;
   }

   private Value addValue(Value value) {
      /*Set<Value> values = testExecution.getValues();
      if (values == null) {
         values = new ArrayList<Value>();
         testExecution.setValues(values);
      }
      values.add(value);
      */
      //TODO: fix this
      return value;
   }

   /**
    * Add a new {@link Value}
    *
    * @return new {@link ValueBuilder}
    */
   public ValueBuilder value() {
      return new ValueBuilder(this, addValue(new Value()));
   }

   /**
    * Shortcut for value().metricName(metricName).resultValue(value).parameter(paramName,
    * paramValue).execution()
    *
    * @param metricName
    * @param value
    * @param paramName
    * @param paramValue
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder value(String metricName, Double value, String paramName, String paramValue) {
      return value().metricName(metricName).resultValue(value).parameter(paramName, paramValue).execution();
   }

   /**
    * Shortcut for value().metricName(metricName).resultValue(value).execution()
    *
    * @param metricName
    * @param value
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder value(String metricName, Double value) {
      return value().metricName(metricName).resultValue(value).execution();
   }

   /**
    * Sets the id of the {@link Test} referenced by this {@link TestExecution}. If the test is not
    * present, it's created.
    *
    * @param testId
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder testId(Long testId) {
      Test test = testExecution.getTest();
      if (test == null) {
         test = new Test();
         testExecution.setTest(test);
      }
      test.setId(testId);
      return this;
   }

   /**
    * Sets the uid of the {@link Test} referenced by this {@link TestExecution}. If the test is not
    * present, it's created.
    *
    * @param testUid
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder testUid(String testUid) {
      Test test = testExecution.getTest();
      if (test == null) {
         test = new Test();
         testExecution.setTest(test);
      }
      test.setUid(testUid);
      return this;
   }

   /**
    * Creates a new {@link TestExecution} based on defined values.
    *
    * @return the {@link TestExecution}
    */
   public TestExecution build() {
      return testExecution;
   }
}
