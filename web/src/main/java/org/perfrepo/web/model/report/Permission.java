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
package org.perfrepo.web.model.report;

import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;
import org.perfrepo.web.model.Entity;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "permission")
public class Permission implements Entity<Permission> {

   private static final long serialVersionUID = 5637370080321126750L;

   @Id
   @SequenceGenerator(name = "PERMISSION_ID_GENERATOR", sequenceName = "PERMISSION_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISSION_ID_GENERATOR")
   private Long id;

   @Column(name = "access_type")
   @Enumerated(EnumType.STRING)
   private AccessType accessType;

   @Column(name = "access_level")
   @Enumerated(EnumType.STRING)
   private AccessLevel level;

   @ManyToOne
   @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = true, updatable = true)
   private Group group;

   @ManyToOne
   @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true, updatable = true)
   private User user;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "report_id", referencedColumnName = "id")
   private Report report;

   public AccessType getAccessType() {
      return accessType;
   }

   public void setAccessType(AccessType permission) {
      this.accessType = permission;
   }

   public AccessLevel getLevel() {
      return level;
   }

   public void setLevel(AccessLevel level) {
      this.level = level;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @Override
   public Long getId() {
      return id;
   }

   public Group getGroup() {
      return group;
   }

   public void setGroup(Group group) {
      this.group = group;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public Report getReport() {
      return report;
   }

   public void setReport(Report report) {
      this.report = report;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Permission)) return false;

      Permission that = (Permission) o;

      if (getAccessType() != that.getAccessType()) return false;
      if (getLevel() != that.getLevel()) return false;
      if (getGroup() != null ? !getGroup().equals(that.getGroup()) : that.getGroup() != null) return false;
      return getUser() != null ? getUser().equals(that.getUser()) : that.getUser() == null;
   }

   @Override
   public int hashCode() {
      int result = getAccessType() != null ? getAccessType().hashCode() : 0;
      result = 31 * result + (getLevel() != null ? getLevel().hashCode() : 0);
      result = 31 * result + (getGroup() != null ? getGroup().hashCode() : 0);
      result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Permission{" +
              "accessType=" + accessType +
              ", group=" + group +
              ", id=" + id +
              ", level=" + level +
              (report != null ? ", reportId=" + report.getId() : "") +
              ", user=" + user +
              '}';
   }
}
