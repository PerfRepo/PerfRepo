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

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link Metric}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
public class MetricDAO extends DAO<Metric, Long> {

   public List<Metric> getMetrics() {
      return getAll();
   }

   /**
    * Returns all metrics by name prefix, which belong tests with defined group id
    *
    * @param name
    * @param groupId
    * @return List of {@link Metric}
    */
   public List<Metric> getMetricByNameAndGroup(String name, String groupId) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("groupId", groupId);
      params.put("name", name);
      return findByNamedQuery(Metric.FIND_BY_NAME_GROUPID, params);
   }

   /**
    * Returns all metrics which belong tests with defined group id
    *
    * @param groupId
    * @return List of {@link Metric}
    */
   public List<Metric> getMetricByGroup(String groupId) {
      Map<String, Object> params = new HashMap();
      params.put("groupId", groupId);
      return findByNamedQuery(Metric.FIND_BY_GROUPID, params);
   }

}