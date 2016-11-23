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
package org.perfrepo.web.controller.reports;

import org.apache.log4j.Logger;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.controller.reports.charts.RfChartSeries;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.session.TEComparatorSession;
import org.perfrepo.web.util.MultiValue;
import org.perfrepo.web.util.MultiValue.ParamInfo;
import org.perfrepo.web.util.MultiValue.ValueInfo;
import org.perfrepo.web.viewscope.ViewScoped;
import org.richfaces.model.ChartDataModel;
import org.richfaces.model.ChartDataModel.ChartType;
import org.richfaces.model.NumberChartDataModel;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Simple comparison of test execution values.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@ViewScoped
public class CompareExecutionsController extends BaseController {

   private static Logger log = Logger.getLogger(CompareExecutionsController.class);

   private static final long serialVersionUID = 1L;
   private static final DecimalFormat FMT = new DecimalFormat("##.0000");
   private static final DecimalFormat FMT_PERCENT = new DecimalFormat("0.00");
   private static final Comparator<String> COMPARE_PARAM_VALUE = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
         try {
            return Double.valueOf(o1).compareTo(Double.valueOf(o2));
         } catch (Exception e) {
            return o1.compareTo(o2);
         }
      }
   };

   @Inject
   private TestService service;

   @Inject
   private TestExecutionService testExecutionService;

   @Inject
   private TEComparatorSession teComparator;

   private List<TestExecution> testExecutions = null;
   private Map<Long, Map<String, ValueInfo>> values = null;
   private Test test = null;
   private TestExecution baselineExecution = null;

   private List<String> multiValueCompareList;
   private String multiValueCompareMetric;
   private String multiValueCompareParam;
   private List<String> multiValueCompareParamList;
   private List<RfChartSeries> multiValueChart;

   private boolean showMultiValueTable = false;

   public Test getTest() {
      return test;
   }

   public TestExecution getBaselineExecution() {
      return baselineExecution;
   }

   public void setAsBaseline(Long execId) {
      //TODO: solve this
      //TestExecution baselineExec1 = EntityUtils.findById(testExecutions, execId);
      TestExecution baselineExec1 = null;
      if (baselineExec1 == null) {
         throw new IllegalStateException("Can't find execution " + execId);
      }
      baselineExecution = baselineExec1;
   }

   public void removeFromComparison(Long execId) {
      teComparator.remove(execId);
      //TODO: solve this
      //TestExecution execToRemove = EntityUtils.findById(testExecutions, execId);
      TestExecution execToRemove = null;
      if (baselineExecution == execToRemove) {
         baselineExecution = null;
      }
      if (execToRemove != null && testExecutions != null) {
         testExecutions.remove(execToRemove);
      }
   }

   public boolean isBaseline(Long execId) {
      return baselineExecution != null && baselineExecution.getId().equals(execId);
   }

   public List<String> getMultiValueCompareList() {
      return multiValueCompareList;
   }

   public List<RfChartSeries> getMultiValueChart() {
      return multiValueChart;
   }

   public String getMultiValueCompareParam() {
      return multiValueCompareParam;
   }

   public void setMultiValueCompareParam(String multiValueCompareParam) {
      this.multiValueCompareParam = multiValueCompareParam;
   }

   public List<String> getMultiValueCompareParamList() {
      return multiValueCompareParamList;
   }

   public String getMultiValueCompareMetric() {
      return multiValueCompareMetric;
   }

   public boolean isShowMultiValueTable() {
      return showMultiValueTable;
   }

   public void setShowMultiValueTable(boolean showMultiValueTable) {
      this.showMultiValueTable = showMultiValueTable;
   }

   private Map<Long, Map<String, ValueInfo>> computeValues() {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return null;
      }
      Map<Long, Map<String, ValueInfo>> r = new TreeMap<Long, Map<String, ValueInfo>>();
      for (TestExecution testExecution : testExecutions) {
         List<ValueInfo> valueInfos = MultiValue.createFrom(testExecution);
         Map<String, ValueInfo> valueInfosForExec = new TreeMap<String, MultiValue.ValueInfo>();
         for (ValueInfo valueInfo : valueInfos) {
            valueInfosForExec.put(valueInfo.getMetricName(), valueInfo);
         }
         r.put(testExecution.getId(), valueInfosForExec);
      }
      return r;
   }

   public String getMetricValue(Long execId, String metricName) {
      ValueInfo valueInfo = findValueInfo(execId, metricName);
      if (valueInfo == null) {
         return "N/A";
      }
      if (valueInfo.isMultiValue()) {
         return "MULTIVALUE";
      } else {
         return valueInfo.getSimpleValue() == null ? "N/A" : FMT.format(valueInfo.getSimpleValue());
      }
   }

   public String getMetricBaselinedValue(Long execId, String metricName) {
      if (baselineExecution == null) {
         return "N/A";
      }
      ValueInfo val = findValueInfo(execId, metricName);
      ValueInfo valBase = findValueInfo(baselineExecution.getId(), metricName);
      if (val == null || valBase == null) {
         return "N/A";
      }
      if (val.isMultiValue() || valBase.isMultiValue()) {
         return "MULTIVALUE";
      }
      if (val.getSimpleValue() == null || valBase.getSimpleValue() == null) {
         return "N/A";
      }
      double dv = val.getSimpleValue();
      double dvbase = valBase.getSimpleValue();
      return FMT_PERCENT.format(((dv - dvbase) / dvbase) * 100d) + " %";
   }

   private ValueInfo findValueInfo(Long execId, String metricName) {
      if (values == null) {
         return null;
      }
      Map<String, ValueInfo> valueInfosForExec = values.get(execId);
      if (valueInfosForExec == null) {
         return null;
      }
      return valueInfosForExec.get(metricName);
   }

   private Double getSimpleNum(Long execId, String metricName) {
      ValueInfo val = findValueInfo(execId, metricName);
      return val == null ? null : val.getSimpleValue();
   }

   private Double getSimpleBaselineNum(Long execId, String metricName) {
      if (baselineExecution == null) {
         return null;
      }
      ValueInfo val = findValueInfo(execId, metricName);
      ValueInfo valBase = findValueInfo(baselineExecution.getId(), metricName);
      if (val == null || valBase == null || val.getSimpleValue() == null || valBase.getSimpleValue() == null) {
         return null;
      }
      double dv = val.getSimpleValue();
      double dvbase = valBase.getSimpleValue();
      return ((dv - dvbase) / dvbase) * 100d;
   }

   private Long checkCommonTestId(List<TestExecution> execs) {
      if (execs == null || execs.isEmpty()) {
         return null;
      }
      Long commonTestId = execs.iterator().next().getTest().getId();
      for (TestExecution exec : execs) {
         if (!commonTestId.equals(exec.getTest().getId())) {
            return null;
         }
      }
      return commonTestId;
   }

   public String getPermaLink() {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/reports/compare/exec?q=" + ParamUtil.generateExecQuery(testExecutions);
   }

   /**
    * called on preRenderView
    */
   public void preRender() {
      reloadSessionMessages();
      if (testExecutions != null) {
         Set<Long> idsInComparator = new HashSet<Long>(teComparator.getExecIds());
         //TODO: solve this
         //Set<Long> idsDisplayed = new HashSet<Long>(EntityUtils.extractIds(testExecutions));
         Set<Long> idsDisplayed = null;
         if (!idsInComparator.equals(idsDisplayed)) {
            testExecutions = null;
            test = null;
            values = null;
         }
      }

      if (testExecutions == null) {
         List<Long> execIdList = ParamUtil.parseExecQuery(getRequestParam("q"));
         if (execIdList == null) {
            execIdList = new ArrayList<Long>(teComparator.getExecIds());
         } else {
            teComparator.clear();
            teComparator.getExecIds().addAll(execIdList);
            execIdList = new ArrayList<Long>(teComparator.getExecIds());
         }
         if (execIdList.isEmpty()) {
            addMessage(INFO, "page.compareExecs.nothingToCompare");
            return;
         }
         Collections.sort(execIdList);
         //TODO: solve this
         //testExecutions = testExecutionService.getTestExecutions(execIdList);
         // update teComparator, some of the executions might not exist anymore
         teComparator.clear();
         for (TestExecution te : testExecutions) {
            teComparator.add(te.getId());
         }
         Long testId = checkCommonTestId(testExecutions);
         if (testId == null) {
            log.error("Can't compare executions of different tests");
            addMessage(ERROR, "page.compareExecs.errorDifferentTests");
            return;
         }
         test = service.getTest(testId);
         values = computeValues();
      }
   }

   private List<ValueInfo> findValueInfos(String metricName) {
      if (testExecutions == null || values == null) {
         return Collections.emptyList();
      }
      List<ValueInfo> infos = new ArrayList<MultiValue.ValueInfo>(testExecutions.size());
      for (TestExecution exec : testExecutions) {
         ValueInfo vi = findValueInfo(exec.getId(), metricName);
         if (vi != null) {
            infos.add(vi);
         }
      }
      return infos;
   }

   public void showMultiValue(String metricName) {
      if (testExecutions == null || values == null) {
         return;
      }
      Set<String> stringSet = new HashSet<String>();
      List<ValueInfo> infos = findValueInfos(metricName);
      for (ValueInfo vi : infos) {
         stringSet.addAll(vi.getComplexValueParams());
      }
      multiValueCompareParamList = new ArrayList<String>(stringSet);
      Collections.sort(multiValueCompareParamList);
      if (multiValueCompareParamList.isEmpty()) {
         multiValueCompareParamList = null;
         return;
      }
      multiValueCompareParam = multiValueCompareParamList.get(0);
      multiValueCompareMetric = metricName;
      updateParamValues(stringSet, infos);
   }

   private void updateParamValues(Set<String> stringSet, List<ValueInfo> infos) {
      stringSet.clear();
      for (ValueInfo vi : infos) {
         List<ParamInfo> cv = vi.getComplexValueByParamName(multiValueCompareParam);
         if (cv != null) {
            for (ParamInfo pi : cv) {
               stringSet.add(pi.getParamValue());
            }
         }
      }
      multiValueCompareList = new ArrayList<String>(stringSet);
      Collections.sort(multiValueCompareList, COMPARE_PARAM_VALUE);
      computeMultiValueChart();
   }

   private void computeMultiValueChart() {
      multiValueChart = new ArrayList<RfChartSeries>();

      for (TestExecution testExecution : testExecutions) {

         ChartDataModel chartDataModel = new NumberChartDataModel(ChartType.line);

         for (String item : multiValueCompareList) {
            chartDataModel.put(Integer.parseInt(item), getMultiValueCompare(testExecution.getId(), item));
         }

         RfChartSeries newSeries = new RfChartSeries(chartDataModel);
         newSeries.setName(testExecution.getName());
         multiValueChart.add(newSeries);
      }
   }

   public Double getMultiValueCompare(Long execId, String paramValue) {
      if (multiValueCompareMetric == null || multiValueCompareParam == null) {
         return null;
      }
      ValueInfo vi = findValueInfo(execId, multiValueCompareMetric);
      if (vi == null) {
         return null;
      }
      List<ParamInfo> cv = vi.getComplexValueByParamName(multiValueCompareParam);
      if (cv == null) {
         return null;
      }
      for (ParamInfo pi : cv) {
         if (paramValue.equals(pi.getParamValue())) {
            return pi.getValue();
         }
      }
      return null;
   }

   public void updateParamSelection() {
      if (multiValueCompareMetric == null || multiValueCompareParam == null) {
         return;
      }
      updateParamValues(new HashSet<String>(), findValueInfos(multiValueCompareMetric));
   }

   public List<Metric> getMetrics() {
      return test == null ? Collections.<Metric>emptyList() : new ArrayList<Metric>(test.getMetrics());
   }

   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public String getTags(TestExecution te) {
      StringBuilder tag = new StringBuilder();
      for (Tag teg : te.getTags()) {
         tag.append(teg.getName()).append("\n");
      }
      return tag.toString().substring(0, tag.length() - 1);
   }
}