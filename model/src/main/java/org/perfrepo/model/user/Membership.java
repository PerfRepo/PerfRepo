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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "user_group")
public class Membership implements Entity<Membership> {

   private static final long serialVersionUID = 4616015836066622075L;

   @Enumerated(EnumType.STRING)
   @Column(name = "type")
   private MembershipType type;

   @Id
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   private User user;

   @Id
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "group_id", referencedColumnName = "id")
   private Group group;

   public Group getGroup() {
      return group;
   }

   public void setGroup(Group group) {
      this.group = group;
   }

   public MembershipType getType() {
      return type;
   }

   public void setType(MembershipType type) {
      this.type = type;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Membership)) return false;

      Membership that = (Membership) o;

      if (getType() != that.getType()) return false;
      if (getUser() != null ? !getUser().equals(that.getUser()) : that.getUser() != null) return false;
      return getGroup() != null ? getGroup().equals(that.getGroup()) : that.getGroup() == null;

   }

   @Override
   public int hashCode() {
      int result = getType() != null ? getType().hashCode() : 0;
      result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
      result = 31 * result + (getGroup() != null ? getGroup().hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Membership{" +
              "group=" + group +
              ", type=" + type +
              ", user=" + user +
              '}';
   }

   @Override
   public Long getId() {
      throw new UnsupportedOperationException("Membership is joining entity, it should not need its ID!!");
   }

   public enum MembershipType {
      REGULAR_USER, GROUP_ADMIN;
   }


}