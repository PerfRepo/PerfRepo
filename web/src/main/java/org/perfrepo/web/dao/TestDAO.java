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

import org.perfrepo.model.Entity;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.userproperty.GroupFilter;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link Test}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class TestDAO extends DAO<Test, Long> {

   /**
    * Retrieves test by UID
    *
    * @param uid
    * @return
    */
   public Test findByUid(String uid) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("uid", uid);

      List<Test> result = findByNamedQuery(Test.FIND_BY_UID, params);
      if (result.size() > 0) {
         return result.get(0);
      }
      return null;
   }

   /**
    * Main search method for tests. All criteria are passed via transfer object.
    *
    * @param search
    * @param userGroupNames
    * @return
    */
   public SearchResultWrapper<Test> searchTests(TestSearchTO search, List<String> userGroupNames) {
      CriteriaQuery<Test> criteria = createCriteria();

      int lastQueryResultsCount = processSearchCountQuery(search, userGroupNames);

      Root<Test> root = criteria.from(Test.class);
      criteria.select(root);
      List<Predicate> predicates = createSearchPredicates(search, root, userGroupNames);

      if (!predicates.isEmpty()) {
         criteria.where(predicates.toArray(new Predicate[0]));
      }

      setOrderBy(criteria, search.getOrderBy(), root);

      TypedQuery<Test> query = query(criteria);
      int firstResult = search.getLimitFrom() == null ? 0 : search.getLimitFrom();
      query.setFirstResult(firstResult);
      if (search.getLimitHowMany() != null) {
         query.setMaxResults(search.getLimitHowMany());
      }

      return new SearchResultWrapper<>(query.getResultList(), lastQueryResultsCount);
   }

   /**
    * Method used primarily for authorization purposes. It retrieves corresponding test
    * that the entity is related to.
    *
    * @param entity
    * @return
    */
   public Test getTestByRelation(Entity<?> entity) {
      Query q = createNamedQuery(entity.getClass().getSimpleName() + ".getTest");
      q.setParameter("entity", entity);
      return ((Test) q.getSingleResult());
   }

   /**
    * Retrieves tests that have provided prefix as a start of its UID.
    *
    * @param prefix
    * @return
    */
   public List<Test> findByUIDPrefix(String prefix) {
      CriteriaQuery<Test> criteria = createCriteria();
      Root<Test> root = criteria.from(Test.class);
      criteria.select(root);
      CriteriaBuilder cb = criteriaBuilder();
      criteria.where(cb.like(root.<String>get("uid"), prefix + "%"));
      return query(criteria).getResultList();
   }

   /**
    * Helper method. Retrieves total number of found tests according to specified
    * search criteria.
    *
    * @param search
    * @param userGroupNames
    * @return
    */
   private int processSearchCountQuery(TestSearchTO search, List<String> userGroupNames) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

      Root<Test> root = countQuery.from(Test.class);
      countQuery.select(cb.countDistinct(root));
      List<Predicate> predicates = createSearchPredicates(search, root, userGroupNames);

      if (!predicates.isEmpty()) {
         countQuery.where(predicates.toArray(new Predicate[0]));
      }

      Long count = query(countQuery).getSingleResult();
      return count.intValue();
   }

   /**
    * Helper method. Since the same predicates are put to search query in count query and actual retrieval query,
    * construction of these predicate is extracted into this method to avoid code duplication.
    *
    * @param search
    * @param root
    * @param userGroupNames
    * @return
    */
   private List<Predicate> createSearchPredicates(TestSearchTO search, Root root, List<String> userGroupNames) {
      CriteriaBuilder cb = criteriaBuilder();
      List<Predicate> predicates = new ArrayList<>();

      if (search.getName() != null && !"".equals(search.getName())) {
         if (search.getName().endsWith("*")) {
            String pattern = search.getName().substring(0, search.getName().length() - 1).concat("%").toLowerCase();
            predicates.add(cb.like(cb.lower(root.<String>get("name")), pattern));
         } else {
            predicates.add(cb.equal(cb.lower(root.<String>get("name")), search.getName()));
         }
      }

      if (search.getUid() != null && !"".equals(search.getUid())) {
         if (search.getUid().endsWith("*")) {
            String pattern = search.getUid().substring(0, search.getUid().length() - 1).concat("%").toLowerCase();
            predicates.add(cb.like(cb.lower(root.<String>get("uid")), pattern));
         } else {
            predicates.add(cb.equal(cb.lower(root.<String>get("uid")), search.getUid()));
         }
      }

      if (search.getGroupId() != null && !"".equals(search.getGroupId())) {
         predicates.add(cb.equal(root.get("groupId"), search.getGroupId()));
      }

      if (GroupFilter.MY_GROUPS.equals(search.getGroupFilter())) {
         predicates.add(cb.and(root.get("groupId").in(userGroupNames)));
      }

      return predicates;
   }

   /**
    * Sets ordering to search query depending on specified parameters.
    *
    * @param criteria
    * @param orderBy
    * @param root
    */
   private void setOrderBy(CriteriaQuery criteria, OrderBy orderBy, Root root) {
      CriteriaBuilder cb = criteriaBuilder();

      Order order;
      switch (orderBy) {
         case NAME_ASC:
            order = cb.asc(root.get("name"));
            break;
         case NAME_DESC:
            order = cb.desc(root.get("name"));
            break;
         case UID_ASC:
            order = cb.asc(root.get("uid"));
            break;
         case UID_DESC:
            order = cb.desc(root.get("uid"));
            break;
         case GROUP_ID_ASC:
            order = cb.asc(root.get("groupId"));
            break;
         case GROUP_ID_DESC:
            order = cb.desc(root.get("groupId"));
            break;
         default:
            order = cb.asc(root.get("name"));
      }

      criteria.orderBy(order);
   }
}