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

import org.perfrepo.model.user.User;
import org.perfrepo.web.service.UserService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

/**
 * Holds information about user.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@SessionScoped
public class UserSessionBean implements UserSession, Serializable {

   private static final long serialVersionUID = 1487959021438612784L;

   @Inject
   private UserService userService;

   @Resource
   private SessionContext sessionContext;

   private User user;
   private Map<String, String> userProperties;

   @PostConstruct
   public void init() {
      refresh();
   }

   public void refresh() {
      if (getLoggedUser() != null) {
         user = userService.getUser(getLoggedUser().getId());
         userProperties = userService.getUserProperties(user);
      }
   }

   public User getLoggedUser() {
      Principal principal = sessionContext.getCallerPrincipal();
      User user = null;
      if (principal != null) {
         user = userService.getUser(principal.getName());
      }
      return user;
   }
}
