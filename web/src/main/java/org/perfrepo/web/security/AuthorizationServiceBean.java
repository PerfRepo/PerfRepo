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
package org.perfrepo.web.security;

import org.perfrepo.model.Entity;
import org.perfrepo.model.Test;
import org.perfrepo.model.auth.AccessLevel;
import org.perfrepo.model.auth.AccessType;
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.PermissionDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.service.GroupService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.Collection;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuthorizationServiceBean implements AuthorizationService {

   @Inject
   private TestDAO testDAO;

   @Inject
   private PermissionDAO permissionDAO;

   @Inject
   private UserService userService;

   @Inject
   private GroupService groupService;

   @Inject
   private UserSession userSession;

   private boolean isUserAuthorizedForReport(Long userId, AccessType accessType, Report report) {
      Collection<Permission> permissions = permissionDAO.getByReport(report.getId());
      if (permissions != null) {
         for (Permission permission : permissions) {
            // if the user require read access, it is not important if the permission is for read or write
            // if the user require write access, the permission must be for write
            if (accessType.equals(AccessType.READ) || permission.getAccessType().equals(AccessType.WRITE)) {
               if (permission.getLevel().equals(AccessLevel.PUBLIC)) {
                  //Public permission, the access is granted
                  return true;
               } else if (permission.getUserId() != null && permission.getLevel().equals(AccessLevel.USER)) {
                  //USER permission, the permission userId should be same as user, who require the access
                  if (userId != null && userId.equals(permission.getUserId())) {
                     return true;
                  }
               } else if (permission.getGroupId() != null && permission.getLevel().equals(AccessLevel.GROUP)) {
                  //GROUP permission, user must be assigned in permission group
                  if (groupService.isUserInGroup(userService.getUser(userId), groupService.getGroup(permission.getGroupId()))) {
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

   private boolean isUserAuthorizedForTest(User user, AccessType accessType, Test test) {
      return groupService.isUserInGroup(user, test.getGroup());
   }

   @Override
   public boolean isUserAuthorizedFor(User user, AccessType accessType, Entity<?> entity) {
      User managedUser = userService.getUser(user.getId());
      if (managedUser.isSuperAdmin()) {
         return true;
      }

      if (entity instanceof Test) {
        return isUserAuthorizedForTest(user, accessType, (Test) entity);
      } else {
         throw new UnsupportedOperationException("Not implemented yet.");
      }
   }

   @Override
   public boolean isUserAuthorizedFor(AccessType accessType, Entity<?> entity) {
      User user = userSession.getLoggedUser();
      return isUserAuthorizedFor(user, accessType, entity);
   }
}
