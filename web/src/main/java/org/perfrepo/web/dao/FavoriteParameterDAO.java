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

import org.perfrepo.model.FavoriteParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link org.perfrepo.model.FavoriteParameter}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class FavoriteParameterDAO extends DAO<FavoriteParameter, Long> {

   public FavoriteParameter findByTestAndParamName(String paramName, Long testId, Long userId) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("paramName", paramName);
      params.put("userId", userId);
      params.put("testId", testId);

      List<FavoriteParameter> result = findByNamedQuery(FavoriteParameter.FIND_BY_TEST_AND_PARAM_NAME, params);
      return !result.isEmpty() ? result.get(0) : null;
   }

   public List<FavoriteParameter> findByTest(Long testId, Long userId) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("userId", userId);
      params.put("testId", testId);

      List<FavoriteParameter> result = findByNamedQuery(FavoriteParameter.FIND_BY_TEST, params);
      return result;
   }
}