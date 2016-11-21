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
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "test_metric",
           joinColumns = {@JoinColumn(name = "test_id", nullable = false, updatable = false)},
           inverseJoinColumns = {@JoinColumn(name = "metric_id", nullable = false, updatable = false)}
   )
   private Set<Metric> metrics = new HashSet<>();

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
       name = "test_subscriber",
       joinColumns = {@JoinColumn(name = "test_id", nullable = false, updatable = false)},
       inverseJoinColumns = {@JoinColumn(name = "user_id", nullable = false, updatable = false)}
   )
   private Set<User> subscribers = new HashSet<>();

   @Column(name = "uid", unique = true)
   @NotNull(message = "{page.test.uidRequired}")
   @Size(max = 2047)
   private String uid;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "group_id", referencedColumnName = "id")
   private Group group;

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

   @XmlTransient
   public Collection<User> getSubscribers() {
      return subscribers;
   }

   @XmlElementWrapper(name = "metrics")
   @XmlElement(name = "metric")
   public Set<Metric> getMetrics() {
      return metrics;
   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   @XmlAttribute(name = "uid")
   public String getUid() {
      return this.uid;
   }

   public Group getGroup() {
      return group;
   }

   public void setGroup(Group group) {
      this.group = group;
   }

   @XmlElement(name = "description")
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setMetrics(Set<Metric> metrics) {
      this.metrics = metrics;
   }

   public void setSubscribers(Set<User> subscribers) {
      this.subscribers = subscribers;
   }

   @XmlTransient
   public List<Metric> getSortedMetrics() {
      Collection<Metric> tags = getMetrics();
      if (tags == null) {
         return new ArrayList<>();
      }

      return tags.stream().sorted(((o1, o2) -> o1.compareTo(o2))).collect(Collectors.toList());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Test)) return false;

      Test test = (Test) o;

      return getUid() != null ? getUid().equals(test.getUid()) : test.getUid() == null;
   }

   @Override
   public int hashCode() {
      return getUid() != null ? getUid().hashCode() : 0;
   }

   public static TestBuilder builder() {
      return new TestBuilder();
   }

}
