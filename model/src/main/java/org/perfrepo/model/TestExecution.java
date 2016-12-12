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

import org.perfrepo.model.builder.TestExecutionBuilder;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents one execution of a test.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "test_execution")
@NamedQueries({@NamedQuery(name = TestExecution.GET_TEST, query = "SELECT te.test from TestExecution te inner join te.test where te= :entity")})
public class TestExecution implements Entity<TestExecution> {

   private static final long serialVersionUID = -2956845045583534606L;

   public static final String GET_TEST = "TestExecution.getTest";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_ID_GENERATOR", sequenceName = "TEST_EXECUTION_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull(message = "{page.testExecution.nameRequired}")
   @Size(max = 2047)
   private String name;

   @ManyToOne(optional = false)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   private Test test;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "test_execution_tag",
           joinColumns = {@JoinColumn(name = "test_execution_id", nullable = false, updatable = false)},
           inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false, updatable = false)}
   )
   private Set<Tag> tags = new HashSet<>();

   @NotNull(message = "{page.testExecution.startedRequired}")
   @Column(name = "started")
   private Date started = new Date();

   @Column(name = "comment")
   @Size(max = 10239)
   private String comment;

   @OneToMany(mappedBy = "testExecution")
   @MapKey(name = "name")
   private Map<String, TestExecutionParameter> parameters;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   public Test getTest() {
      return this.test;
   }

   public Set<Tag> getTags() {
      return this.tags;
   }

   public void setTags(Set<Tag> tags) {
      this.tags = tags;
   }

   public Date getStarted() {
      return started;
   }

   public void setStarted(Date started) {
      this.started = started;
   }

   public Map<String, TestExecutionParameter> getParameters() {
      return parameters;
   }

   public void setParameters(Map<String, TestExecutionParameter> parameters) {
      this.parameters = parameters;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public static TestExecutionBuilder builder() {
      return new TestExecutionBuilder();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof TestExecution)) return false;

      TestExecution that = (TestExecution) o;

      return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
   }

   @Override
   public int hashCode() {
      return getId() != null ? getId().hashCode() : 0;
   }

   @Override
   public String toString() {
      return "TestExecution{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", comment='" + comment + '\'' +
              ", started=" + started +
              ", tags=" + tags +
              '}';
   }
}
