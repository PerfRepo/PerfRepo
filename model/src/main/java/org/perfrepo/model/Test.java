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
import org.perfrepo.model.builder.TestBuilder;
import org.perfrepo.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents one test.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "test")
@SecuredEntity(type = EntityType.TEST)
@NamedQueries({
    @NamedQuery(name = Test.FIND_BY_UID, query = "SELECT test FROM Test test WHERE test.uid = :uid")
})
@XmlRootElement(name = "test")
public class Test implements Entity<Test> {

   private static final long serialVersionUID = 2936849220074718535L;

   public static final String FIND_BY_UID = "Test.findByUid";

   @Id
   @SequenceGenerator(name = "TEST_ID_GENERATOR", sequenceName = "TEST_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull(message = "{page.test.nameRequired}")
   @Size(max = 2047)
   private String name;

   @OneToMany(mappedBy = "test")
   private Collection<TestExecution> testExecutions;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "test_metric",
           joinColumns = {@JoinColumn(name = "test_id", nullable = false, updatable = false)},
           inverseJoinColumns = {@JoinColumn(name = "metric_id", nullable = false, updatable = false)}
   )
   private Collection<Metric> metrics;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
       name = "test_subscriber",
       joinColumns = {@JoinColumn(name = "test_id", nullable = false, updatable = false)},
       inverseJoinColumns = {@JoinColumn(name = "user_id", nullable = false, updatable = false)}
   )
   private Collection<User> subscribers;

   @OneToMany(mappedBy = "test")
   private Collection<Alert> alerts;

   @Column(name = "uid")
   @NotNull(message = "{page.test.uidRequired}")
   @Size(max = 2047)
   private String uid;

   @Column(name = "groupId")
   @NotNull(message = "{page.test.groupRequired}")
   @Size(max = 255)
   private String groupId;

   @Column(name = "description")
   @Size(max = 10239)
   private String description;

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @XmlID
   @XmlAttribute(name = "id")
   public String getStringId() {
      return id == null ? null : String.valueOf(id);
   }

   public void setStringId(String id) {
      this.id = Long.valueOf(id);
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setTestExecutions(Collection<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   @XmlTransient
   public Collection<TestExecution> getTestExecutions() {
      return this.testExecutions;
   }

   public Collection<User> getSubscribers() {
      return subscribers;
   }

   public void setSubscribers(Collection<User> testSubscribers) {
      this.subscribers = testSubscribers;
   }

   @XmlTransient
   public Collection<Alert> getAlerts() {
      return alerts;
   }

   public void setAlerts(Collection<Alert> alerts) {
      this.alerts = alerts;
   }

   @XmlElementWrapper(name = "metrics")
   @XmlElement(name = "metric")
   public Collection<Metric> getMetrics() {
      return metrics;
   }

   public void setMetrics(Collection<Metric> metrics) {
      this.metrics = metrics;
   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   @XmlAttribute(name = "uid")
   public String getUid() {
      return this.uid;
   }

   @XmlAttribute(name = "groupId")
   public String getGroupId() {
      return groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public String getDescription() {
      return description;
   }

   @XmlElement(name = "description")
   public void setDescription(String description) {
      this.description = description;
   }

   @XmlTransient
   public List<Metric> getSortedMetrics() {
      Collection<Metric> tags = getMetrics();
      if (tags == null) {
         return new ArrayList<>();
      }

      return tags.stream().sorted(((o1, o2) -> o1.compareTo(o2))).collect(Collectors.toList());
   }

   public static TestBuilder builder() {
      return new TestBuilder();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((uid == null) ? 0 : uid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Test other = (Test) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (groupId == null) {
         if (other.groupId != null)
            return false;
      } else if (!groupId.equals(other.groupId))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (uid == null) {
         if (other.uid != null)
            return false;
      } else if (!uid.equals(other.uid))
         return false;
      return true;
   }

   @Override
   public Test clone() {
      try {
         return (Test) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }
}
