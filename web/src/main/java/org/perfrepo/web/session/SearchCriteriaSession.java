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

import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.service.search.TestSearchCriteria;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * Session storage for search criteria.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@SessionScoped
public class SearchCriteriaSession implements Serializable {

   private static final long serialVersionUID = 9050986914006178498L;

   private TestExecutionSearchTO executionSearchCriteria;

   private TestSearchCriteria testSearchCriteria;

   public TestExecutionSearchTO getExecutionSearchCriteria() {
      if (executionSearchCriteria == null) {
         executionSearchCriteria = new TestExecutionSearchTO();

         //by default, do not list all the test executions
         executionSearchCriteria.setLimitHowMany(50);
         executionSearchCriteria.setOrderBy(OrderBy.DATE_DESC);
      }
      return executionSearchCriteria;
   }

   public void clearExecutionSearchCriteria() {
      executionSearchCriteria = null;
      getExecutionSearchCriteria();
   }

   public void clearTestSearchCriteria() {
      testSearchCriteria = null;
      getTestSearchCriteria();
   }

   public TestSearchCriteria getTestSearchCriteria() {
      if (testSearchCriteria == null) {
         testSearchCriteria = new TestSearchCriteria();
         testSearchCriteria.setOrderBy(OrderBy.NAME_ASC);
         testSearchCriteria.setLimitHowMany(25);
      }

      return testSearchCriteria;
   }
}
