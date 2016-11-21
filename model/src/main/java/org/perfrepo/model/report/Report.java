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
package org.perfrepo.model.report;

import org.perfrepo.model.Entity;
import org.perfrepo.model.auth.EntityType;
import org.perfrepo.model.auth.SecuredEntity;
import org.perfrepo.model.user.User;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Map;

/**
 * Represents a report entity.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "report")
@SecuredEntity(type = EntityType.REPORT)
@XmlRootElement(name = "report")
@NamedQueries({
    @NamedQuery(name = Report.GET_BY_USERNAME, query = "SELECT distinct report from Report report join report.user user where user.username = :username"),
    @NamedQuery(name = Report.GET_BY_GROUP_PERMISSION, query = "SELECT distinct report FROM Permission permission INNER JOIN permission.report report WHERE permission.groupId in (:groupIds) or permission.userId= :userId"),
    @NamedQuery(name = Report.GET_BY_ANY_PERMISSION, query = "SELECT distinct report FROM Permission permission INNER JOIN permission.report report WHERE permission.level = 'PUBLIC' or permission.groupId in (:groupIds) or permission.userId = :userId"),
    @NamedQuery(name = Report.FIND_MAX_ID, query = "SELECT max(report.id) from Report report")
})
public class Report implements Entity<Report>, Comparable<Report> {

   private static final long serialVersionUID = -2188625358440509257L;

   public static final String GET_BY_USERNAME = "Report.findByUserName";
   public static final String GET_BY_GROUP_PERMISSION = "Report.getByGroupPermission";
   public static final String GET_BY_ANY_PERMISSION = "Report.getByAnyPermission";
   public static final String FIND_MAX_ID = "Report.findMaxId";

   @Id
   @SequenceGenerator(name = "REPORT_ID_GENERATOR", sequenceName = "REPORT_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORT_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 255)
   private String name;

   @Column(name = "type")
   @NotNull
   @Size(max = 255)
   private String type;

   @ManyToOne
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   @NotNull
   private User user;

   @OneToMany(mappedBy = "report")
   @MapKey(name = "name")
   private Map<String, ReportProperty> properties;

   @Transient
   private String username;

   public Report() {
      super();
   }

   public Report(Long id) {
      this.id = id;
   }

   @XmlAttribute(name = "id")
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "type")
   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   @XmlTransient
   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   @XmlElementWrapper(name = "properties")
   @XmlElement(name = "property")
   public Map<String, ReportProperty> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, ReportProperty> properties) {
      this.properties = properties;
   }

   @XmlAttribute(name = "user")
   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Report)) return false;

      Report report = (Report) o;

      if (getName() != null ? !getName().equals(report.getName()) : report.getName() != null) return false;
      if (getType() != null ? !getType().equals(report.getType()) : report.getType() != null) return false;
      if (getUser() != null ? !getUser().equals(report.getUser()) : report.getUser() != null) return false;
      return getProperties() != null ? getProperties().equals(report.getProperties()) : report.getProperties() == null;
   }

   @Override
   public int hashCode() {
      int result = getName() != null ? getName().hashCode() : 0;
      result = 31 * result + (getType() != null ? getType().hashCode() : 0);
      result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
      result = 31 * result + (getProperties() != null ? getProperties().hashCode() : 0);
      return result;
   }

   @Override
   public int compareTo(Report o) {
      return this.getName().compareTo(o.getName());
   }

   @Override
   public String toString() {
      return "Report{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", type='" + type + '\'' +
              '}';
   }
}