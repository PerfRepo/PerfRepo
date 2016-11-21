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
package org.perfrepo.web.controller.reports;

import org.perfrepo.model.auth.AccessLevel;
import org.perfrepo.model.auth.AccessType;
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.security.AuthorizationService;
import org.perfrepo.web.service.GroupService;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Named
@ViewScoped
public class ReportPermissionController extends BaseController {

   @Inject
   private ReportService reportService;

   @Inject
   private UserService userService;

   @Inject
   private GroupService groupService;

   @Inject
   private UserSession userSession;

   @Deprecated
   private Collection<Permission> permissionsOld = new ArrayList<Permission>();
   @Deprecated
   private Collection<Permission> workingCopy = new ArrayList<Permission>();
   @Deprecated
   private String userId;
   @Deprecated
   private String groupId;
   @Deprecated
   private Permission permission = new Permission();

   private Long reportId;
   private boolean isPanelShown = false;
   private boolean userAuthorized = false;

   //part after refactoring
   private List<Permission> permissions;

   private List<User> usersForSelection;
   private List<Group> groupsForSelection;

   @Inject
   private AuthorizationService authorizationService;

   @PostConstruct
   public void initialize() {
      String reportIdParam = getRequestParam("reportId");
      reportId = reportIdParam != null ? Long.parseLong(reportIdParam) : null;
      //this will be removed, currently present for backwards compatibility
      permissionsOld = reportService.getReportPermissions(new Report(reportId));
      permissions = new ArrayList<>(reportService.getReportPermissions(new Report(reportId)));
      if (reportId != null) {
         userAuthorized = authorizationService.isUserAuthorizedFor(AccessType.WRITE, new Report(reportId));
      } else {
         userAuthorized = true;
      }
   }

   public List<User> getUsersForSelection() {
      if (usersForSelection == null) {
         usersForSelection = userService.getAllUsers();
      }

      return usersForSelection;
   }

   public List<Group> getGroupsForSelection() {
      if (groupsForSelection == null) {
         groupsForSelection = groupService.getAllGroups();
      }

      return groupsForSelection;
   }

   public void addPermission() {
      if (permissions == null) {
         permissions = new ArrayList<>();
      }

      Permission permission = new Permission();
      permission.setLevel(AccessLevel.PUBLIC);
      permission.setAccessType(AccessType.READ);

      permissions.add(permission);
   }

   public void removePermission(Permission permission) {
      permissions.remove(permission);
   }

   public void initDefaultPermissions() {
      if (permissions == null) {
         permissions = new ArrayList<>();

         Permission permission = new Permission();
         permission.setLevel(AccessLevel.GROUP);
         permission.setAccessType(AccessType.WRITE);

         Collection<Group> groups = userService.getUser(userSession.getLoggedUser().getId()).getGroups();
         permission.setGroupId(groups.stream().findFirst().get().getId());

         permissions.add(permission);
      }
   }

   public AccessLevel[] getAccessLevels() {
      return AccessLevel.values();
   }

   public AccessType[] getAccessTypes() {
      return AccessType.values();
   }

   public List<Permission> getPermissions() {
      return permissions;
   }

   public void setPermissions(List<Permission> permissions) {
      this.permissions = permissions;
   }

   public Long getReportId() {
      return reportId;
   }

   public void setReportId(Long reportId) {
      this.reportId = reportId;
   }

   public void togglePanel() {
      isPanelShown = !isPanelShown;
   }

   public boolean isPanelShown() {
      return isPanelShown;
   }

   public void setPanelShown(boolean isPanelShown) {
      this.isPanelShown = isPanelShown;
   }

   public boolean isUserAuthorized() {
      return userAuthorized;
   }

   public void setUserAuthorized(boolean userAuthorized) {
      this.userAuthorized = userAuthorized;
   }

   @Deprecated
   public String getPermissionGroup(Permission p) {
      if (AccessLevel.GROUP.equals(p.getLevel())) {
         Group group = groupService.getGroup(p.getGroupId());
         return group.getName();
      } else {
         return null;
      }
   }

   @Deprecated
   public String getPermissionUser(Permission p) {
      if (AccessLevel.USER.equals(p.getLevel())) {
         User user = userService.getUser(p.getUserId());
         return user.getUsername();
      } else {
         return null;
      }
   }

   @Deprecated
   public Collection<Permission> getPermissionsOld() {
      return permissionsOld;
   }

   @Deprecated
   public void setPermissionsOld(Collection<Permission> permissions) {
      this.permissionsOld = permissions;
   }

   @Deprecated
   public void clearWorkingCopy() {
      workingCopy.clear();
   }

   @Deprecated
   public void clearPermission() {
      permission = new Permission();
      userId = "";
      groupId = "";
   }

   @Deprecated
   public void removePermissionOld(Permission p) {
      permissionsOld.remove(p);
   }

   @Deprecated
   public void addNewPermission() {
      if (AccessLevel.GROUP.equals(permission.getLevel())) {
         permission.setGroupId(Long.valueOf(groupId));
      } else if (AccessLevel.USER.equals(permission.getLevel())) {
         permission.setUserId(Long.valueOf(userId));
      }
      permissionsOld.add(permission);
      permission = new Permission();
      userId = "";
      groupId = "";
   }

   @Deprecated
   public Collection<Permission> getWorkingCopy() {
      return workingCopy;
   }

   @Deprecated
   public void setWorkingCopy(Collection<Permission> workingCopy) {
      this.workingCopy = workingCopy;
   }

   @Deprecated
   public String getUserId() {
      return userId;
   }

   @Deprecated
   public void setUserId(String userId) {
      this.userId = userId;
   }

   @Deprecated
   public String getGroupId() {
      return groupId;
   }

   @Deprecated
   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   @Deprecated
   public Permission getPermission() {
      return permission;
   }

   @Deprecated
   public void setPermission(Permission permission) {
      this.permission = permission;
   }
}