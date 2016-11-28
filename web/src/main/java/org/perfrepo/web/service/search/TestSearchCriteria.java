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
package org.perfrepo.web.service.search;

import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.user.Group;

import java.io.Serializable;
import java.util.Set;

/**
 * Test search criteria. TODO: revisit the documentation
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class TestSearchCriteria implements Serializable {

   private static final long serialVersionUID = -2549234530526625783L;

   private String name;

   private String uid;

   private Set<Group> groups;

   private OrderBy orderBy = OrderBy.NAME_ASC;

   private Integer limitFrom;

   private Integer limitHowMany;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getUid() {
      return uid;
   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   public Set<Group> getGroups() {
      return groups;
   }

   public void setGroups(Set<Group> groups) {
      this.groups = groups;
   }

   public OrderBy getOrderBy() {
      return orderBy;
   }

   public void setOrderBy(OrderBy orderBy) {
      this.orderBy = orderBy;
   }

   public Integer getLimitFrom() {
      return limitFrom;
   }

   public void setLimitFrom(Integer limitFrom) {
      this.limitFrom = limitFrom;
   }

   public Integer getLimitHowMany() {
      return limitHowMany;
   }

   public void setLimitHowMany(Integer limitHowMany) {
      this.limitHowMany = limitHowMany;
   }
}
