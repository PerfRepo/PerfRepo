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
package org.perfrepo.web.controller.reports.boxplot;

import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.controller.reports.ReportPermissionController;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.reports.BoxplotReportServiceBean;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for boxplot reports
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named("boxplotReportBean")
@ViewScoped
public class BoxplotReportController extends BaseController {

   @Inject
   private TestService testService;

   @Inject
   private BoxplotReportServiceBean reportService;

   @Inject
   private ReportPermissionController reportPermissionController;

   //properties bound to specific report being displayed
   private String name;
   private List<Chart> charts;

   private List<Test> testsForSelection;

   /**
    * Called directly from template allowing to display messages.
    */
   public void init() {
      reloadSessionMessages();
   }


   @PostConstruct
   public void preloadReportWhenEditing() {
      reloadSessionMessages();
      if (getReportId() != null) {
         load();
      }
   }

   public void save() {
      Long reportId = getReportId();
      if (reportId == null) {
         reportId = reportService.create(name, charts, reportPermissionController.getPermissions());
      } else {
         reportId = reportService.update(reportId, name, charts, reportPermissionController.getPermissions());
      }

      if (reportId != null) {
         redirectWithMessage("/reports/boxplot/" + reportId, INFO, "page.reports.boxplot.reportSaved");
      } else {
         redirectWithMessage("/reports", ERROR, "page.reports.boxplot.reportNotSaved");
      }
   }

   public void load() {
      Long reportId = getReportId();
      if (reportId == null) {
         throw new IllegalArgumentException("No report ID provided.");
      }

      Map<String, List<Chart>> loadedReport = null;
      try {
         loadedReport = reportService.load(reportId);
         name = loadedReport.keySet().stream().findFirst().get();
         charts = loadedReport.get(name);
      } catch (IllegalArgumentException ex) {
         redirectWithMessage("/reports", ERROR, "page.report.error");
      } catch (Exception e) {
         if (e.getCause() instanceof SecurityException) {
            redirectWithMessage("/reports", ERROR, "page.report.permissionDenied");
         } else {
            redirectWithMessage("/reports", ERROR, "page.report.erro");
         }
      }
   }

   public void computeCharts() {
      reportService.computeCharts(charts);
   }

   public List<Test> getTestsForSelection() {
      if (testsForSelection == null) {
         //TODO: solve this
         //testsForSelection = testService.getAvailableTests().getResult();
         testsForSelection = null;
         Collections.sort(testsForSelection, (o1, o2) -> o1.getName().compareTo(o2.getName()));
      }

      return testsForSelection;
   }

   public void addChart() {
      if (charts == null) {
         charts = new ArrayList<>();
      }

      Chart chart = new Chart();
      chart.setxAxisLabel(Chart.AxisOption.DATE);
      chart.setxAxisSort(Chart.AxisOption.DATE);
      charts.add(chart);
   }

   public void removeChart(Chart chart) {
      charts.remove(chart);
   }

   public void addSeries(Chart chart) {
      Chart.Series series = new Chart.Series();
      chart.addSeries(series);
   }

   public void removeSeries(Chart chart, Chart.Series series) {
      chart.removeSeries(series);
   }

   public void addBaseline(Chart chart) {
      Chart.Baseline baseline = new Chart.Baseline();
      chart.addBaseline(baseline);
   }

   public void removeBaseline(Chart chart, Chart.Baseline baseline) {
      chart.removeBaseline(baseline);
   }

   public List<Metric> getMetricsForSelection(Chart chart) {
      Test test = new Test();
      test.setId(chart.getTestId());
      return testService.getMetricsForTest(test);
   }

   public List<Chart> getCharts() {
      return charts;
   }

   public void setCharts(List<Chart> charts) {
      this.charts = charts;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Long getReportId() {
      return getRequestParamLong("reportId");
   }
}