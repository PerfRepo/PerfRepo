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
package org.perfrepo.web.service.util;

import org.perfrepo.model.user.User;
import org.perfrepo.web.session.UserSession;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@SessionScoped
public class UserSessionMock implements UserSession, Serializable {

   private static final long serialVersionUID = 1487959021438612784L;

   private static User user;

   public static void setLoggedUser(User testUser) {
      user = testUser;
   }

   public static User getLoggedUserStatic() {
      return user;
   }

   public User getLoggedUser() {
      return user;
   }
}
