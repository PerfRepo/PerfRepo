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

import org.perfrepo.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@javax.persistence.Entity
@Table(name = "user_property")
@NamedQueries({
        @NamedQuery(name = UserProperty.GET_BY_USER, query = "SELECT property FROM UserProperty property WHERE property.user.id = :userId")
})
public class UserProperty implements Entity<UserProperty>, Comparable<UserProperty> {

   private static final long serialVersionUID = -1476383380689021931L;

   public static final String GET_BY_USER = "UserProperty.getByUser";

   @Id
   @SequenceGenerator(name = "USER_PROPERTY_ID_GENERATOR", sequenceName = "USER_PROPERTY_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PROPERTY_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @Column(name = "value")
   @NotNull
   @Size(max = 2047)
   private String value;

   @ManyToOne(optional = false)
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   @NotNull
   private User user;

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

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   @Override
   public int compareTo(UserProperty o) {
      return this.name.compareTo(o.name);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UserProperty)) return false;

      UserProperty that = (UserProperty) o;

      if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
      return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
   }

   @Override
   public int hashCode() {
      int result = getName() != null ? getName().hashCode() : 0;
      result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "UserProperty{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", value='" + value + '\'' +
              '}';
   }
}