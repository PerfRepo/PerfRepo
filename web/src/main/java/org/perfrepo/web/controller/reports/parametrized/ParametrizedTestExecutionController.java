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
package org.perfrepo.web.controller.reports.parametrized;

import com.google.common.collect.Lists;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Detail of list of {@link TestExecution} grouped by test definition and job Id
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 */
@Named
@ViewScoped
public class ParametrizedTestExecutionController extends BaseController {

   private static final long serialVersionUID = 3012075520261954430L;

   @Inject
   private TestService testService;

   private static final Logger log = Logger.getLogger(ParametrizedTestExecutionController.class);

   private Long testId;

   private Long jobId;

   private List<TestExecution> testExecutions = null;

   private Map<String, String> renderedParam;

   private Test test = null;

   private Long compareJobId;

   private TestExecutionSearchTO search = new TestExecutionSearchTO();

   private List<TestExecution> result;

   private TestExecutionTable table = new TestExecutionTable();

   private boolean markedBestResult = true;

   //TODO: filter parameters - only changeable should be in the table
   //TODO: complete graphs
   //TODO: possibility to change all values or single value - best result - to create baseline
   //TODO: TAGS

   @PostConstruct
   public void preRender() {
      reloadSessionMessages();
      testId = getRequestParamLong("testId");
      jobId = getRequestParamLong("jobId");
      if (testId != null && jobId != null) {
         test = testService.getTest(testId);
         //testExecutions = testService.getFullTestExecutionsByTestAndJob(testId, jobId);
         table.process(jobId, testExecutions);
         renderedParam = new HashMap<String, String>();
         //<a4j:mediaOutput element="img" expires="#{now}" cacheable="#{false}" createContent="#{jfreechart.generate}" mimeType="image/png"
         //value="#{parametrizedTestExecutionController.chart}" />
      } else {
         redirectWithMessage("/", ERROR, "page.exec.errorNoTestId");
      }
   }

   public void addCompareTestExecutions() {
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      String[] jobs = request.getParameterValues("toAdd");
      if (jobs != null && jobs.length > 0) {
         for (String job : jobs) {
            addCompareTestExecutions(Long.valueOf(job));
         }
      }
   }

   public void addCompareTestExecutions(Long jobId) {
      if (table.getCompareJobIds() == null || table.getCompareJobIds().size() == 0) {
         compareJobId = jobId;
      }
      List<TestExecution> compareTestExecutions = null;
      table.addTestExecutions(jobId, compareTestExecutions);
   }

   public float compare(String metricName, MultiKey parameters) {
      if (compareJobId != null) {
         return table.compareValues(compareJobId, metricName, parameters);
      }
      return 0;
   }

   public String getStyle(float number) {
      if (compareJobId != null) {
         if (number < -3) {
            return "red";
         } else if (number < 0) {
            return "orange";
         } else {
            return "green";
         }
      } else {
         return "black";
      }
   }

   public void transferBestValues() {
      table.transferBestValues();
   }

   public String getStyle(String metricName, MultiKey parameters) {
      if (compareJobId != null) {
         float number = table.compareValues(compareJobId, metricName, parameters);
         if (isMarkedBestResult()) {
            if (table.isBestResult(parameters, metricName)) {
               return "blue";
            }
         }
         return getStyle(number);
      } else {
         return "black";
      }
   }

   public String getStyle(Long jobId, String metricName, MultiKey parameters) {
      if (isMarkedBestResult()) {
         if (table.isBestResult(jobId, parameters, metricName)) {
            return "blue";
         }
      }
      return "black";
   }

   public List<String> getTags() {
      return Lists.newArrayList(table.getTags().get(jobId));
   }

   public List<String> getTags(Long jobId) {
      return Lists.newArrayList(table.getTags().get(jobId));
   }

   public List<MultiKey> getSortedRowKeys() {
      return table.getSortedRowKeys();
   }

   public Value getValue(Long jobId, String metricName, MultiKey parameters) {
      return table.getValue(jobId, metricName, parameters);
   }

   public Value getValue(String metricName, MultiKey parameters) {
      return table.getValue(metricName, parameters);
   }

   public void searchTEs() {
      search.setTestUID(test.getUid());
      //result = testService.searchTestExecutionsGroupedByJobId(search);
   }

   public void removeCompareTE(Long jobId) {
      if (jobId.equals(compareJobId)) {
         compareJobId = null;
      }
      table.removeTestExecutions(jobId);
   }

   public boolean renderCell(MultiKey key, int index) {
      String paramValue = String.valueOf(key.getKey(index));
      String paramName = getParameterNames().get(index);
      if (renderedParam.get(paramName) == null || !renderedParam.get(paramName).equals(paramValue)) {
         renderedParam.put(paramName, paramValue);
         return true;
      }
      return false;
   }

   public int getRowSpan(MultiKey key, int index) {
      return table.getParameterValuesCount(key, index);
   }

   public List<TestExecution> getTestExecution() {
      return testExecutions;
   }

   public Test getTest() {
      return test;
   }

   public Long getTestId() {
      return testId;
   }

   public void setTestId(Long testId) {
      this.testId = testId;
   }

   public Long getJobId() {
      return jobId;
   }

   public void setJobId(Long jobId) {
      this.jobId = jobId;
   }

   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public void setTestExecutions(List<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   public List<String> getParameterNames() {
      return table.getParameterNames();
   }

   public List<Metric> getMetrics() {
      return table.getMetrics();
   }

   public List<Long> getCompareJobsId() {
      return table.getCompareJobIds();
   }

   public Long getCompareJobId() {
      return compareJobId;
   }

   public void setCompareJobId(Long compareJobId) {
      this.compareJobId = compareJobId;
   }

   public List<TestExecution> getResult() {
      return result;
   }

   public void setResult(List<TestExecution> result) {
      this.result = result;
   }

   public TestExecutionSearchTO getSearch() {
      return search;
   }

   public void setSearch(TestExecutionSearchTO search) {
      this.search = search;
   }

   public boolean isMarkedBestResult() {
      return markedBestResult;
   }

   public void setMarkedBestResult(boolean markedBestResult) {
      this.markedBestResult = markedBestResult;
   }

}