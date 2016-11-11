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
package org.perfrepo.model;

import org.perfrepo.model.auth.EntityType;
import org.perfrepo.model.auth.SecuredEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@javax.persistence.Entity
@Table(name = "test_execution_parameter")
@SecuredEntity(type = EntityType.TEST, parent = "testExecution")
@NamedQueries({
    @NamedQuery(name = TestExecutionParameter.GET_TEST, query = "SELECT test from Test test inner join test.testExecutions te inner join te.parameters tep where tep = :entity"),
    @NamedQuery(name = TestExecutionParameter.FIND_BY_TEST_ID, query = "SELECT DISTINCT p.name FROM TestExecutionParameter p, TestExecution e WHERE p.testExecution.id = e.id AND e.test.id = :testId")})
@XmlRootElement(name = "testExecutionParameter")
public class TestExecutionParameter implements Entity<TestExecutionParameter>, Comparable<TestExecutionParameter> {

   private static final long serialVersionUID = -5534543562306898358L;

   public static final String FIND_ALL = "TestExecutionParameter.findAll";

   public static final String GET_TEST = "TestExecutionParameter.getTest";
   public static final String FIND_BY_TEST_ID = "TestExecutionParameter.findByTestId";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_PARAMETER_ID_GENERATOR", sequenceName = "TEST_EXECUTION_PARAMETER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_PARAMETER_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @ManyToOne(optional = false)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   @Column(name = "value")
   @NotNull
   @Size(max = 2047)
   private String value;

   public TestExecutionParameter() {
      super();
   }

   public TestExecutionParameter(String name, String value) {
      this.testExecution = new TestExecution();
      this.name = name;
      this.value = value;
   }

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

   @XmlTransient
   public TestExecution getTestExecution() {
      return this.testExecution;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @XmlAttribute(name = "value")
   public String getValue() {
      return this.value;
   }

   @Override
   public int compareTo(TestExecutionParameter o) {
      return this.getName().compareTo(o.getName());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof TestExecutionParameter)) return false;

      TestExecutionParameter parameter = (TestExecutionParameter) o;

      if (getName() != null ? !getName().equals(parameter.getName()) : parameter.getName() != null) return false;
      return getValue() != null ? getValue().equals(parameter.getValue()) : parameter.getValue() == null;
   }

   @Override
   public int hashCode() {
      int result = getName() != null ? getName().hashCode() : 0;
      result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "TestExecutionParameter{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", value='" + value + '\'' +
              '}';
   }
}