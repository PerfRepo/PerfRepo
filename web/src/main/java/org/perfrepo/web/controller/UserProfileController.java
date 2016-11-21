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
package org.perfrepo.web.controller;

import org.perfrepo.model.user.User;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Backing bean user profile.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
@ViewScoped
public class UserProfileController extends BaseController {

   @Inject
   private UserService userService;

   @Inject
   private UserSession userSession;

   private User user;

   private String oldPassword;
   private String newPassword;
   private String newPasswordConfirmation;

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
      user = userService.getUser(userSession.getUser().getId());
   }

   public User getUser() {
      return user;
   }

   public void update() {
      if (user == null) {
         throw new IllegalArgumentException("User cannot be null.");
      }

      try {
         User updatedUser = userService.updateUser(user);
         redirectWithMessage("/profile", INFO, "page.user.updatedSuccesfully");
      } catch (ServiceException ex) {
         addMessage(ex);
      } catch (org.perfrepo.web.security.SecurityException ex) {
         addMessage(ex);
      }
   }

   public void changePassword() {
      if (user == null) {
         throw new IllegalArgumentException("User cannot be null.");
      }

      if (newPassword == null || !newPassword.equals(newPasswordConfirmation)) {
         addMessage(ERROR, "page.user.newPasswordsNotEqual");
         return;
      }

      /*try {*/
         //TODO: solve this
         //userService.changePassword(oldPassword, newPassword);
      /*   redirectWithMessage("/profile", INFO, "page.user.updatedSuccesfully");
      } catch (ServiceException ex) {
         addMessage(ERROR, "page.user.oldPasswordDoesntMatch");
      }*/
   }

   public void setOldPassword(String oldPassword) {
      this.oldPassword = oldPassword;
   }

   public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
   }

   public void setNewPasswordConfirmation(String newPasswordConfirmation) {
      this.newPasswordConfirmation = newPasswordConfirmation;
   }

   public String getOldPassword() {
      return oldPassword;
   }

   public String getNewPassword() {
      return newPassword;
   }

   public String getNewPasswordConfirmation() {
      return newPasswordConfirmation;
   }
}
