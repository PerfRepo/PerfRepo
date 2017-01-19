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
package org.perfrepo.web.model.to;

import org.perfrepo.enums.GroupFilter;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.model.Tag;
import org.perfrepo.web.model.user.Group;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Test execution search criteria.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionSearchCriteria {

   private Set<Long> ids = new HashSet<>();
   private Date startedFrom; //means "Test executed after"
   private Date startedTo; //means "Test executed before"
   private Set<Tag> tags = new HashSet<>();
   private Set<Group> groups = new HashSet<>();
   private String testUID;
   private String testName;
   private Map<String, String> parameters = new HashMap<>();
   private Integer limitFrom;
   private Integer limitHowMany;
   private GroupFilter groupFilter;
   private OrderBy orderBy = OrderBy.DATE_DESC;
   private String orderByParameter;
   private String labelParameter;

   public GroupFilter getGroupFilter() {
      return groupFilter;
   }

   public void setGroupFilter(GroupFilter groupFilter) {
      this.groupFilter = groupFilter;
   }

   public Set<Group> getGroups() {
      return groups;
   }

   public void setGroups(Set<Group> groups) {
      this.groups = groups;
   }

   public Set<Long> getIds() {
      return ids;
   }

   public void setIds(Set<Long> ids) {
      this.ids = ids;
   }

   public String getLabelParameter() {
      return labelParameter;
   }

   public void setLabelParameter(String labelParameter) {
      this.labelParameter = labelParameter;
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

   public OrderBy getOrderBy() {
      return orderBy;
   }

   public void setOrderBy(OrderBy orderBy) {
      this.orderBy = orderBy;
   }

   public String getOrderByParameter() {
      return orderByParameter;
   }

   public void setOrderByParameter(String orderByParameter) {
      this.orderByParameter = orderByParameter;
   }

   public Map<String, String> getParameters() {
      return parameters;
   }

   public void setParameters(Map<String, String> parameters) {
      this.parameters = parameters;
   }

   public Date getStartedFrom() {
      return startedFrom;
   }

   public void setStartedFrom(Date startedFrom) {
      this.startedFrom = startedFrom;
   }

   public Date getStartedTo() {
      return startedTo;
   }

   public void setStartedTo(Date startedTo) {
      this.startedTo = startedTo;
   }

   public Set<Tag> getTags() {
      return tags;
   }

   public void setTags(Set<Tag> tags) {
      this.tags = tags;
   }

   public String getTestName() {
      return testName;
   }

   public void setTestName(String testName) {
      this.testName = testName;
   }

   public String getTestUID() {
      return testUID;
   }

   public void setTestUID(String testUID) {
      this.testUID = testUID;
   }
}
