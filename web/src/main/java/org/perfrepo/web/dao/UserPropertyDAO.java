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
import org.perfrepo.model.user.User;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

   public UserProperty findByUserIdAndName(Long userId, String name) {
      CriteriaQuery<UserProperty> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();
      Root<UserProperty> rUserProperty = criteria.from(UserProperty.class);
      Join<UserProperty, User> rUser = rUserProperty.join("user");

      Predicate pUser = cb.equal(rUser.get("id"), cb.parameter(Long.class, "userId"));
      Predicate pName = cb.equal(rUserProperty.get("name"), cb.parameter(String.class, "name"));
      criteria.select(rUserProperty);
      criteria.where(cb.and(pUser, pName));

      TypedQuery<UserProperty> query = query(criteria);
      query.setParameter("userId", userId);
      query.setParameter("name", name);

      List<UserProperty> props = query.getResultList();
      if (props.isEmpty()) {
         return null;
      } else {
         return props.get(0);
      }
   }

   public List<UserProperty> findByUserName(String userName) {
      CriteriaQuery<UserProperty> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();
      Root<UserProperty> rUserProperty = criteria.from(UserProperty.class);
      Join<UserProperty, User> rUser = rUserProperty.join("user");
      Predicate pUser = cb.equal(rUser.get("username"), cb.parameter(String.class, "username"));
      criteria.select(rUserProperty);
      criteria.where(pUser);
      TypedQuery<UserProperty> query = query(criteria);
      query.setParameter("username", userName);
      return query.getResultList();
   }

   public List<UserProperty> findByUserName(String userName, String propertyPrefix) {
      CriteriaQuery<UserProperty> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();
      Root<UserProperty> rUserProperty = criteria.from(UserProperty.class);
      Join<UserProperty, User> rUser = rUserProperty.join("user");
      Predicate pUser = cb.equal(rUser.get("username"), cb.parameter(String.class, "username"));
      Predicate pName = cb.like(rUserProperty.<String>get("name"), cb.parameter(String.class, "name"));
      criteria.select(rUserProperty);
      criteria.where(cb.and(pUser, pName));
      TypedQuery<UserProperty> query = query(criteria);
      query.setParameter("username", userName);
      query.setParameter("name", propertyPrefix + "%");
      return query.getResultList();
   }

   public void deletePropertiesFromUser(Long userId) {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("userId", userId);

      List<UserProperty> properties = findByNamedQuery(UserProperty.GET_BY_USER, parameters);
      properties.stream().forEach(property -> remove(property));

      // the flush is needed because we usually want to see the results of the deletion
      // even during the opened transaction
      entityManager().flush();
   }
}