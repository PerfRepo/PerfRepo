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
package org.perfrepo.web.controller;

import org.perfrepo.model.Test;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.search.TestSearchCriteria;
import org.perfrepo.web.session.SearchCriteriaSession;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Search tests.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
@ViewScoped
public class TestSearchController extends BaseController {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testService;

   @Inject
   private SearchCriteriaSession criteriaSession;

   @Inject
   private UserSession userSession;

   private List<Test> result;

   private int resultsPageNumber = 1;
   private int totalNumberOfResults;
   private int totalNumberOfResultsPages;

   @PostConstruct
   public void init() {
      resultsPageNumber = 1;
      search();
   }

   public void search() {
      TestSearchCriteria criteria = criteriaSession.getTestSearchCriteria();
      //criteria.setGroupFilter(userSession.getGroupFilter());
      criteria.setLimitHowMany(criteria.getLimitHowMany() <= 0 ? null : criteria.getLimitHowMany());
      criteria.setLimitFrom(criteria.getLimitHowMany() == null ? null : (resultsPageNumber - 1) * criteria.getLimitHowMany());

      SearchResultWrapper<Test> searchResult = testService.searchTests(criteria);
      result = searchResult.getResult();
      totalNumberOfResults = searchResult.getTotalSearchResultsCount();

      constructPagination();
   }

   public String delete() {
      Long idToDelete = Long.valueOf(getRequestParam("idToDelete"));
      if (idToDelete == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      }

      Test testToRemove = removeById(idToDelete);
      if (testToRemove == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      }

      //try {
      //testService.removeTest(testToRemove);
      addMessage(INFO, "page.testSearch.testDeleted", testToRemove.getName());
      //} catch (ServiceException e) {
      //   addMessage(e);
      //}

      search();
      return null;
   }

   public void orderBy(String how) {
      OrderBy newOrderBy = null;
      OrderBy oldOrderBy = criteriaSession.getTestSearchCriteria().getOrderBy();

      if (how.equals("name")) {
         if (oldOrderBy == OrderBy.NAME_ASC) {
            newOrderBy = OrderBy.NAME_DESC;
         } else {
            newOrderBy = OrderBy.NAME_ASC;
         }
      } else if (how.equals("uid")) {
         if (oldOrderBy == OrderBy.UID_ASC) {
            newOrderBy = OrderBy.UID_DESC;
         } else {
            newOrderBy = OrderBy.UID_ASC;
         }
      } else if (how.equals("groupId")) {
         if (oldOrderBy == OrderBy.GROUP_ID_ASC) {
            newOrderBy = OrderBy.GROUP_ID_DESC;
         } else {
            newOrderBy = OrderBy.GROUP_ID_ASC;
         }
      } else {
         newOrderBy = OrderBy.NAME_ASC;
      }

      criteriaSession.getTestSearchCriteria().setOrderBy(newOrderBy);
      search();
   }

   public List<String> autocompleteTest(String test) {
      //return testService.getTestsByUidPrefix(test);
      return null;
   }

   public void setGroupFilter(GroupFilter groupFilter) {
      //userSession.setGroupFilter(groupFilter);
      criteriaChanged();
      search();
   }

   public void criteriaChanged() {
      resultsPageNumber = 1;
   }

   private Test removeById(Long id) {
      for (Test test : result) {
         if (test.getId().equals(id)) {
            result.remove(test);
            return test;
         }
      }
      return null;
   }

   /** ----- Functions for pagination ----- **/

   public void changeHowMany(ValueChangeEvent e) {
      TestSearchCriteria criteria = criteriaSession.getTestSearchCriteria();
      criteria.setLimitHowMany((Integer) e.getNewValue());

      search();
   }

   public void changeResultsPageNumber(int page) {
      this.resultsPageNumber = page;

      search();
   }

   private void constructPagination() {
      TestSearchCriteria criteria = criteriaSession.getTestSearchCriteria();

      this.resultsPageNumber = (criteria.getLimitFrom() == null) ? 1 : (criteria.getLimitFrom() / criteria.getLimitHowMany()) + 1;
      computeTotalNumberOfPages();
   }

   private void computeTotalNumberOfPages() {
      Integer howMany = criteriaSession.getTestSearchCriteria().getLimitHowMany();

      if (howMany == null) {
         totalNumberOfResultsPages = 1;
         return;
      }

      totalNumberOfResultsPages = (totalNumberOfResults / howMany) + (totalNumberOfResults % howMany != 0 ? 1 : 0);
   }

   /*** ---- Getters/Setters ----- ***/

   public List<Test> getResult() {
      return result;
   }

   public void setResult(List<Test> result) {
      this.result = result;
   }

   public int getResultsPageNumber() {
      return resultsPageNumber;
   }

   public int getTotalNumberOfResults() {
      return totalNumberOfResults;
   }

   public int getTotalNumberOfResultsPages() {
      return totalNumberOfResultsPages;
   }
}