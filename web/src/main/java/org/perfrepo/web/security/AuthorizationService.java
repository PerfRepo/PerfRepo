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
import org.perfrepo.model.auth.AccessType;
import org.perfrepo.model.user.User;

/**
 * Authorization Service is responsible for permission verification on entities
 *
 * @author Pavel Drozd
 */
public interface AuthorizationService {

   /**
    * Verifies if the specific user is authorized to entity
    *
    * @param user
    * @param accessType
    * @param entity
    * @return boolean
    */
   public boolean isUserAuthorizedFor(User user, AccessType accessType, Entity<?> entity);

   /**
    * Verifies if the logged user is authorized to entity
    *
    * @param accessType
    * @param entity
    * @return boolean
    */
   public boolean isUserAuthorizedFor(AccessType accessType, Entity<?> entity);
}
