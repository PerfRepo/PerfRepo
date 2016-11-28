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
package org.perfrepo.web.dao;

import org.perfrepo.model.UserProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link UserProperty}
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UserPropertyDAO extends DAO<UserProperty, Long> {

   public List<UserProperty> findByUserId(Long userId) {
      return getAllByProperty("user", userId);
   }

   public void deletePropertiesFromUser(Long userId) {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("userId", userId);

      List<UserProperty> properties = findByNamedQuery(UserProperty.GET_BY_USER, parameters);
      properties.stream().forEach(property -> remove(property));

      // the flush is needed because we want to see the results of the deletion
      // even during the opened transaction
      // this is related to the way we update user properties - delete them all and replace them from scratch
      // in a single transaction
      entityManager().flush();
   }
}