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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Common ancestor for DAO objects
 *
 * @param <T> entity type
 * @param <PK> primary key type
 * @author Pavel Drozd
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public abstract class DAO<T extends Entity<T>, PK extends Serializable> {

   @PersistenceContext(unitName = "PerfRepoPU")
   private EntityManager em;

   private Class<T> type;

   @SuppressWarnings("unchecked")
   public DAO() {
      if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
         type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      } else {
         type = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
      }
   }

   /**
    * Find an entity based on it's primary key
    *
    * @param id Primary key
    * @return Entity
    */
   public T get(final PK id) {
      return em.find(type, id);
   }

   public T merge(final T entity) {
      T stored = em.merge(entity);

      return stored;
   }

   public T create(final T entity) {
      em.persist(entity);

      return entity;
   }

   public void remove(final T entity) {
      em.remove(entity);
   }

   /**
    * Finds all entities of current type
    *
    * @return all entities
    */
   public List<T> getAll() {
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      return em.createQuery(criteria).getResultList();
   }

   /**
    * Finds all entities of current type with restriction to one property
    *
    * @param propertyName
    * @param value
    * @return all entities with selected property
    */
   public List<T> getAllByProperty(final String propertyName, final Object value) {
      CriteriaQuery<T> criteria = createCriteria();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      criteria.where(em.getCriteriaBuilder().equal(root.get(propertyName), value));
      return em.createQuery(criteria).getResultList();
   }

   /**
    * Finds all entities of current type with restriction to one property which has a value
    * in the provided collection
    *
    * @param propertyName
    * @param value
    * @return all entities with selected property
    */
   public List<T> getAllByPropertyIn(final String propertyName, final Collection<Object> value) {
      CriteriaQuery<T> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();
      Root<T> root = criteria.from(type);
      criteria.select(root);
      criteria.where(root.get(propertyName).in(cb.parameter(Collection.class, "value")));
      TypedQuery<T> query = em.createQuery(criteria);
      query.setParameter("value", value);

      return query.getResultList();
   }

   /**
    * Return result of named query
    *
    * @param queryName
    * @param queryParams
    * @return List of entities corresponding to query
    */
   public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
      TypedQuery<T> tq = em.createNamedQuery(queryName, type);
      for (Entry<String, Object> entry : queryParams.entrySet()) {
         tq.setParameter(entry.getKey(), entry.getValue());
      }

      return tq.getResultList();
   }

   public T findWithDepth(Object id, String... fetchRelations) {
      CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
      CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
      Root<T> root = criteriaQuery.from(type);
      for (String relation : fetchRelations) {
         FetchParent<T, T> fetch = root;
         for (String pathSegment : relation.split("\\.")) {
            fetch = fetch.fetch(pathSegment, JoinType.LEFT);
         }
      }
      criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
      return getSingleOrNoneResult(em.createQuery(criteriaQuery));
   }

   /**
    * Creates a typed query from criteria query
    *
    * @param criteria
    * @param <T1>
    * @return typed query
    */
   protected <T1> TypedQuery<T1> query(CriteriaQuery<T1> criteria) {
      return em.createQuery(criteria);
   }

   protected CriteriaQuery<T> createCriteria() {
      return em.getCriteriaBuilder().createQuery(type);
   }

   protected CriteriaBuilder criteriaBuilder() {
      return em.getCriteriaBuilder();
   }

   protected Query createNamedQuery(String name) {
      return em.createNamedQuery(name);
   }

   protected <QueryType> TypedQuery<QueryType> createNamedQuery(String name, Class<QueryType> clazz) {
      return em.createNamedQuery(name, clazz);
   }

   protected EntityManager entityManager() {
      return em;
   }

   private T getSingleOrNoneResult(TypedQuery<T> query) {
      query.setMaxResults(1);
      List<T> result = query.getResultList();
      if (result.isEmpty()) {
         return null;
      }
      return result.get(0);
   }
}