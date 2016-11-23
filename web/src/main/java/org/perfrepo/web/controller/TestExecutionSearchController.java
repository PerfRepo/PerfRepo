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

import org.perfrepo.model.TestExecution;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.SearchCriteriaSession;
import org.perfrepo.web.session.TEComparatorSession;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.util.TagUtils;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search test executions.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@ViewScoped
public class TestExecutionSearchController extends BaseController {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testService;

   @Inject
   private TestExecutionService testExecutionService;

   @Inject
   private UserSession userSession;

   @Inject
   private SearchCriteriaSession criteriaSession;

   @Inject
   private TEComparatorSession comparatorSession;

   private List<TestExecution> result;
   private List<String> paramColumns;

   private String tag;

   private boolean showMassOperations = false;
   private String massOperationAddTags;
   private String massOperationDeleteTags;
   private int massOperationDeleteExecutionsConfirm;

   private int resultsPageNumber = 1;
   private int totalNumberOfResults;
   private int totalNumberOfResultsPages;

   @PostConstruct
   public void init() {
      resultsPageNumber = 1;
      search();
   }

   /**
    * Main method performing search
    */
   public void search() {
      TestExecutionSearchTO criteria = criteriaSession.getExecutionSearchCriteria();
      criteria.setGroupFilter(userSession.getGroupFilter());
      criteria.setLimitHowMany(criteria.getLimitHowMany() <= 0 ? null : criteria.getLimitHowMany());
      criteria.setLimitFrom(criteria.getLimitHowMany() == null ? null : (resultsPageNumber - 1) * criteria.getLimitHowMany());

      SearchResultWrapper<TestExecution> searchResult = testExecutionService.searchTestExecutions(criteria);
      result = searchResult.getResult();
      totalNumberOfResults = searchResult.getTotalSearchResultsCount();

      constructPagination();

      paramColumns = criteria.getParameters().stream().filter(ParamCriteria::isDisplayed).map(ParamCriteria::getName).collect(Collectors.toList());
   }

   public String delete() {
      Long idToDelete = Long.valueOf(getRequestParam("idToDelete"));
      if (idToDelete == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      }

      TestExecution execToRemove = removeById(idToDelete);
      if (execToRemove == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      }

      try {
         testExecutionService.removeTestExecution(execToRemove);
         addMessage(INFO, "page.execSearch.execSuccessfullyDeleted", execToRemove.getName());
      } catch (ServiceException e) {
         addMessage(e);
      }

      return null;
   }

   public String itemParam(TestExecution exec, String paramName) {
      //TODO: solve this
      //return ViewUtils.displayValue(exec.findParameter(paramName));
      return null;
   }

   public void addParameterCriteria() {
      criteriaSession.getExecutionSearchCriteria().getParameters().add(new TestExecutionSearchTO.ParamCriteria());
   }

   public void removeParameterCriteria(TestExecutionSearchTO.ParamCriteria criteriaToRemove) {
      criteriaSession.getExecutionSearchCriteria().getParameters().remove(criteriaToRemove);
   }

   public void orderBy(String how) {
      OrderBy currentOrderBy = criteriaSession.getExecutionSearchCriteria().getOrderBy();
      OrderBy newOrderBy = null;
      if (how.equals("name")) {
         if (currentOrderBy == OrderBy.NAME_ASC) {
            newOrderBy = OrderBy.NAME_DESC;
         } else {
            newOrderBy = OrderBy.NAME_ASC;
         }
      } else if (how.equals("started")) {
         if (currentOrderBy == OrderBy.DATE_ASC) {
            newOrderBy = OrderBy.DATE_DESC;
         } else {
            newOrderBy = OrderBy.DATE_ASC;
         }
      }

      criteriaSession.getExecutionSearchCriteria().setOrderBy(newOrderBy);
      search();
   }

   public List<String> autocompleteTest(String test) {
      return testService.getTestsByPrefix(test);
   }

   public List<String> autocompleteParameter(String parameter) {
      return testExecutionService.getParametersByPrefix(parameter).stream().map(parameter1 -> parameter1.getName()).collect(Collectors.toList());
   }

   public List<String> autocompleteTags(String tag) {
      String returnPrefix = "";
      if (tag.startsWith("-")) {
         tag = tag.substring(1);
         returnPrefix = "-";
      }

      List<String> tmp = testExecutionService.getTagsByPrefix(tag);
      List<String> result = new ArrayList<String>(tmp.size());
      if (!returnPrefix.isEmpty()) {
         for (String item : tmp) {
            result.add(returnPrefix + item);
         }
      } else {
         result.addAll(tmp);
      }

      return result;
   }

   public void setGroupFilter(GroupFilter groupFilter) {
      userSession.setGroupFilter(groupFilter);
      criteriaChanged();
      search();
   }

   public void criteriaChanged() {
      resultsPageNumber = 1;
   }

   private TestExecution removeById(Long id) {
      for (TestExecution exec : result) {
         if (exec.getId().equals(id)) {
            result.remove(exec);
            return exec;
         }
      }
      return null;
   }

   /** ----- Methods for mass operations ---- **/

   public void addAllCurrentResultsToComparison() {
      List<Long> ids = result.stream().map(TestExecution::getId).collect(Collectors.toList());
      ids.stream().forEach(id -> comparatorSession.add(id));
   }

   public void addTagsToFoundTestExecutions() {
      List<String> tags = TagUtils.parseTags(massOperationAddTags != null ? massOperationAddTags.toLowerCase() : "");

      //TODO: solve this
      //testExecutionService.addTagsToTestExecutions(tags, result);
      search();
   }

   public void deleteTagsFromFoundTestExecutions() {
      List<String> tags = TagUtils.parseTags(massOperationDeleteTags != null ? massOperationDeleteTags.toLowerCase() : "");

      //TODO: solve this
      //testExecutionService.removeTagsFromTestExecutions(tags, result);
      search();
   }

   public void deleteFoundTestExecutions() {
      for (TestExecution testExecution : result) {
         try {
            testExecutionService.removeTestExecution(testExecution);
         } catch (ServiceException ex) {
            addMessage(ex);
         }
      }

      search();
   }

   /** ----- Functions for pagination ----- **/

   public void changeHowMany(ValueChangeEvent e) {
      TestExecutionSearchTO criteria = criteriaSession.getExecutionSearchCriteria();
      criteria.setLimitHowMany((Integer) e.getNewValue());

      search();
   }

   public void changeResultsPageNumber(int page) {
      this.resultsPageNumber = page;

      search();
   }

   private void constructPagination() {
      TestExecutionSearchTO criteria = criteriaSession.getExecutionSearchCriteria();

      this.resultsPageNumber = (criteria.getLimitFrom() == null) ? 1 : (criteria.getLimitFrom() / criteria.getLimitHowMany()) + 1;
      computeTotalNumberOfPages();
   }

   private void computeTotalNumberOfPages() {
      Integer howMany = criteriaSession.getExecutionSearchCriteria().getLimitHowMany();

      if (howMany == null) {
         totalNumberOfResultsPages = 1;
         return;
      }

      totalNumberOfResultsPages = (totalNumberOfResults / howMany) + (totalNumberOfResults % howMany != 0 ? 1 : 0);
   }

   /** ----- Getters/Setters ----- **/

   public long getTotalNumberOfResults() {
      return totalNumberOfResults;
   }

   public List<String> getParamColumns() {
      return paramColumns;
   }

   public String getTag() {
      return tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public List<TestExecution> getResult() {
      return result;
   }

   public void setResult(List<TestExecution> result) {
      this.result = result;
   }

   public boolean isShowMassOperations() {
      return showMassOperations;
   }

   public void setShowMassOperations(boolean showMassOperations) {
      this.showMassOperations = showMassOperations;
   }

   public void toggleShowMassOperations() {
      this.showMassOperations = !showMassOperations;
   }

   public String getMassOperationAddTags() {
      return massOperationAddTags;
   }

   public void setMassOperationAddTags(String massOperationAddTags) {
      this.massOperationAddTags = massOperationAddTags;
   }

   public String getMassOperationDeleteTags() {
      return massOperationDeleteTags;
   }

   public void setMassOperationDeleteTags(String massOperationDeleteTags) {
      this.massOperationDeleteTags = massOperationDeleteTags;
   }

   public int getMassOperationDeleteExecutionsConfirm() {
      return massOperationDeleteExecutionsConfirm;
   }

   public void setMassOperationDeleteExecutionsConfirm(int massOperationDeleteExecutionsConfirm) {
      this.massOperationDeleteExecutionsConfirm = massOperationDeleteExecutionsConfirm;
   }

   public int getResultSize() {
      return result != null ? result.size() : 0;
   }


   public long getTotalNumberOfResultsPages() {
      return totalNumberOfResultsPages;
   }

   public long getResultsPageNumber() {
      return resultsPageNumber;
   }
}