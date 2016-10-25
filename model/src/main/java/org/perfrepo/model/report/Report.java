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
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.auth.SecuredEntity;
import org.perfrepo.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collection;
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
    @NamedQuery(name = Report.GET_BY_GROUP_PERMISSION, query = "SELECT distinct report from Report report join report.permissions perm where perm.groupId in (:groupIds) or perm.userId= :userId"),
    @NamedQuery(name = Report.GET_BY_ANY_PERMISSION, query = "SELECT distinct report from Report report join report.permissions perm where perm.level = 'PUBLIC' or perm.groupId in (:groupIds) or perm.userId= :userId"),
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

   @ManyToOne(optional = false, cascade = CascadeType.MERGE)
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   @NotNull
   private User user;

   @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
   @MapKey(name = "name")
   private Map<String, ReportProperty> properties;

   @OneToMany(mappedBy = "report")
   private Collection<Permission> permissions;

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

   @XmlElementWrapper(name = "permissions")
   @XmlElement(name = "permission")
   public Collection<Permission> getPermissions() {
      return permissions;
   }

   public void setPermissions(Collection<Permission> permissions) {
      this.permissions = permissions;
   }

   @XmlAttribute(name = "user")
   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      Report other = (Report) obj;
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
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      return true;
   }

   @Override
   public Report clone() {
      try {
         return (Report) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public int compareTo(Report o) {
      return this.getName().compareTo(o.getName());
   }
}