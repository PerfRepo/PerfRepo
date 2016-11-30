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
package org.perfrepo.model.user;

import org.perfrepo.model.Entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "\"group\"")
@NamedQueries({
        @NamedQuery(name = Group.GET_BY_NAME, query = "SELECT group FROM Group group WHERE group.name = :name"),
        @NamedQuery(name = Group.GET_USER_GROUPS, query = "SELECT membership.group FROM Membership membership WHERE membership.user = :user")
})
public class Group implements Entity<Group>, Comparable<Group> {

   private static final long serialVersionUID = -9158731656089441951L;

   public static final String GET_BY_NAME = "Group.getByName";
   public static final String GET_USER_GROUPS = "Group.getUserGroups";

   @Id
   @SequenceGenerator(name = "GROUP_ID_GENERATOR", sequenceName = "GROUP_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_ID_GENERATOR")
   private Long id;

   @Column(name = "name", unique = true)
   private String name;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Group)) return false;

      Group group = (Group) o;

      return getName() != null ? getName().equals(group.getName()) : group.getName() == null;

   }

   @Override
   public int hashCode() {
      return getName() != null ? getName().hashCode() : 0;
   }

   @Override
   public int compareTo(Group group) {
      return this.name.compareTo(group.name);
   }

   @Override
   public String toString() {
      return "Group{" +
              "id=" + id +
              ", name='" + name + '\'' +
              '}';
   }
}
