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

import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * DAO for {@link Value}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
public class ValueDAO extends DAO<Value, Long> {

   public Value getValue(Long id) {
      return findWithDepth(id, "parameters");
   }

   public List<Value> find(Long execId, Long metricId) {
      CriteriaQuery<Value> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();

      Root<Value> rValue = criteria.from(Value.class);
      Join<Value, Metric> rMetric = rValue.join("metric");
      Join<Value, TestExecution> rExec = rValue.join("testExecution");
      Predicate pFixExec = cb.equal(rMetric.get("id"), cb.parameter(Long.class, "metricId"));
      Predicate pFixMetric = cb.equal(rExec.get("id"), cb.parameter(Long.class, "execId"));

      rValue.fetch("parameters");
      criteria.select(rValue);
      criteria.where(cb.and(pFixExec, pFixMetric));
      TypedQuery<Value> query = query(criteria);
      query.setParameter("metricId", metricId);
      query.setParameter("execId", execId);
      return query.getResultList();
   }
}