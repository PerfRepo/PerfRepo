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
package org.perfrepo.web.session;

import org.perfrepo.model.UserProperty;
import org.perfrepo.model.user.User;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.model.userproperty.ReportFilter;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Holds information about user.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named(value = "userSession")
@SessionScoped
public class UserSession implements Serializable {

   private static final long serialVersionUID = 1487959021438612784L;

   /**
    * User property name of group filter - used to search tests and test executions
    */
   private static final String USER_PARAM_GROUP_FILTER = "test.filter.group";

   /**
    * User property name of report filter used on reports page
    */
   private static final String USER_PARAM_REPORT_FILTER = "report.filter";

   @Inject
   private UserService userService;

   private User user;

   @PostConstruct
   public void init() {
      refresh();
   }

   public void refresh() {
      if (userService.getLoggedUser() != null) {
         user = userService.getFullUser(userService.getLoggedUser().getId());
      }
   }

   /**
    * Returns user stored in session
    * @return
    */
   public User getUser() {
      return user;
   }

   /**
    * Logout the user
    * @return
    */
   public String logout() {
      //FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
      return "HomeRedirect";
   }

   /**
    * Returns the user property {@value #USER_PARAM_GROUP_FILTER}
    * @return
    */
   public GroupFilter getGroupFilter() {
      UserProperty groupFilter = findUserParameter(USER_PARAM_GROUP_FILTER);
      if (groupFilter == null) {
         return GroupFilter.MY_GROUPS;
      } else {
         return GroupFilter.valueOf(groupFilter.getValue());
      }
   }

   /**
    * Set the user property {@value #USER_PARAM_GROUP_FILTER}
    * @param filter
    * @throws ServiceException
    */
   public void setGroupFilter(GroupFilter filter) {
      userService.addUserProperty(USER_PARAM_GROUP_FILTER, filter.name());
      refresh();
   }

   /**
    * Returns the user property {@value #USER_PARAM_GROUP_FILTER}
    * @return
    */
   public ReportFilter getReportFilter() {
      UserProperty reportFilter = findUserParameter(USER_PARAM_REPORT_FILTER);
      if (reportFilter == null) {
         return ReportFilter.TEAM;
      } else {
         return ReportFilter.valueOf(reportFilter.getValue());
      }
   }

   /**
    * Set the user property {@value #USER_PARAM_GROUP_FILTER}
    * @param filter
    * @throws ServiceException
    */
   public void setReportFilter(ReportFilter filter) {
      userService.addUserProperty(USER_PARAM_REPORT_FILTER, filter.name());
      refresh();
   }

   /**
    * Set user property {@value #USER_PARAM_GROUP_FILTER} to {@link GroupFilter.ALL_GROUPS}
    * @throws ServiceException
    */
   public void setAllGroupFilter() throws ServiceException {
      setGroupFilter(GroupFilter.ALL_GROUPS);
   }

   /**
    * Set user property {@value #USER_PARAM_GROUP_FILTER} to {@link GroupFilter.MY_GROUPS}
    * @throws ServiceException
    */
   public void setMyGroupFilter() throws ServiceException {
      setGroupFilter(GroupFilter.MY_GROUPS);
   }

   /**
    * Find user property by name
    * @param name
    * @return
    */
   public UserProperty findUserParameter(String name) {
      for (UserProperty up : user.getProperties()) {
         if (name.equals(up.getName())) {
            return up;
         }
      }
      return null;
   }
}
