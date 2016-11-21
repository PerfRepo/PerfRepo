package org.perfrepo.web.service.reports;

import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;
import org.perfrepo.model.to.MultiValueResultWrapper;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.web.controller.reports.boxplot.Chart;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.util.ReportUtils;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service bean for boxplot report. Contains all necessary method to be able to work with boxplot reports.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BoxplotReportServiceBean {

   public static final String BOXPLOT_REPORT_TYPE = "Boxplot";

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private UserService userService;

   @Inject
   private ReportService reportService;

   @Inject
   private UserSession userSession;

   /**
    * Creates new boxplot report
    *
    * @param name   name of the report
    * @param charts transfer object with all information about the report
    * @return ID of newly created report
    */
   public Long create(String name, List<Chart> charts, List<Permission> permissions) {
      Report report = new Report();
      report.setName(name);
      report.setType(BOXPLOT_REPORT_TYPE);
      //TODO: solve this
      //report.setPermissions(permissions);

      User user = userSession.getLoggedUser();
      report.setUser(user);

      Map<String, ReportProperty> properties = storeTOIntoReportProperties(charts, report);
      report.setProperties(properties);

      Report createdReport = reportService.createReport(report);
      return createdReport.getId();
   }

   /**
    * Updates existing boxplot report.
    *
    * @param reportId
    * @param name
    * @param charts
    * @param permissions
    * @return
    */
   public Long update(Long reportId, String name, List<Chart> charts, List<Permission> permissions) {
      Report report = reportService.getFullReport(new Report(reportId));
      report.setName(name);
      report.setType(BOXPLOT_REPORT_TYPE);
      //TODO: solve this
      //report.setPermissions(permissions);

      Map<String, ReportProperty> properties = storeTOIntoReportProperties(charts, report);
      report.setProperties(properties);

      Report createdReport = reportService.updateReport(report);
      return createdReport.getId();
   }

   /**
    * Loads all Chart transfer objects of specified report from database and report properties.
    *
    * @param reportId
    * @return
    */
   public Map<String, List<Chart>> load(Long reportId) {
      Report report = reportService.getFullReport(new Report(reportId));
      if (report == null) {
         throw new IllegalArgumentException("Report with ID=" + reportId + " doesn't exist");
      }

      Map<String, List<Chart>> result = new HashMap<>();
      result.put(report.getName(), loadTOFromReportProperties(report.getProperties()));

      return result;
   }

   /**
    * Given Chart transfer objects, this method fills them with actual computes values, so after calling this method,
    * all charts contain all necessary information for being displayed.
    *
    * @param charts
    */
   public void computeCharts(List<Chart> charts) {
      if (charts == null) {
         return;
      }

      for (Chart chart : charts) {
         Test test = testDAO.get(chart.getTestId());

         if (chart.getSeriesList() == null) {
            continue;
         }

         for (Chart.Series series : chart.getSeriesList()) {
            TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
            searchCriteria.setTestUID(test.getUid());
            searchCriteria.setTags(series.getTags());

            switch (chart.getxAxisSort()) {
               case DATE:
                  searchCriteria.setOrderBy(OrderBy.DATE_ASC);
                  break;
               case PARAMETER:
                  searchCriteria.setOrderBy(OrderBy.PARAMETER_ASC);
                  break;
               case VERSION:
                  searchCriteria.setOrderBy(OrderBy.VERSION_ASC);
            }

            searchCriteria.setOrderByParameter(chart.getxAxisSortParameter());
            searchCriteria.setLabelParameter(chart.getxAxisLabel() == Chart.AxisOption.PARAMETER ? chart.getxAxisLabelParameter() : null);

            Metric metric = metricDAO.get(series.getMetricId());
            //TODO: solve this
            //List<MultiValueResultWrapper> resultWrappers = testExecutionDAO.searchMultiValues(searchCriteria, metric, userService.getLoggedUserGroupNames());
            List<MultiValueResultWrapper> resultWrappers = null;

            List<Chart.Series.DataPoint> dataPoints = assembleResults(resultWrappers);
            series.setDataPoints(dataPoints);
         }

         if (chart.getBaselines() == null) {
            continue;
         }

         for (Chart.Baseline baseline : chart.getBaselines()) {
            Metric metric = metricDAO.get(baseline.getMetricId());
            Double value = testExecutionDAO.getValueForMetric(baseline.getExecutionId(), metric.getName());

            baseline.setResultValue(value);
         }
      }
   }

   /**
    * Denormalizes all chart transfer objects of single report into report properties.
    *
    * @param charts
    * @param report
    * @return
    */
   private Map<String, ReportProperty> storeTOIntoReportProperties(List<Chart> charts, Report report) {
      Map<String, ReportProperty> properties = new HashMap<>();
      for (int i = 0; i < charts.size(); i++) {
         Chart chart = charts.get(i);
         String chartPrefix = "chart" + i + ".";

         storeChartProperties(properties, chart, chartPrefix, report);
         storeSeries(properties, chart, chartPrefix, report);
         storeBaselines(properties, chart, chartPrefix, report);
      }

      return properties;
   }

   /**
    * Denormalizes top-level Chart transfer object properties into report properties
    *
    * @param properties
    * @param chart
    * @param chartPrefix
    * @param report
    */
   private void storeChartProperties(Map<String, ReportProperty> properties, Chart chart, String chartPrefix, Report report) {
      ReportUtils.createOrUpdateReportPropertyInMap(properties, chartPrefix + "name", chart.getName(), report);
      ReportUtils.createOrUpdateReportPropertyInMap(properties, chartPrefix + "test", chart.getTestId().toString(), report);
      ReportUtils.createOrUpdateReportPropertyInMap(properties, chartPrefix + "xAxisLabelType", chart.getxAxisLabel().toString(), report);
      ReportUtils.createOrUpdateReportPropertyInMap(properties, chartPrefix + "xAxisLabelParameter", chart.getxAxisLabelParameter(), report);
      ReportUtils.createOrUpdateReportPropertyInMap(properties, chartPrefix + "xAxisSortType", chart.getxAxisSort().toString(), report);
      ReportUtils.createOrUpdateReportPropertyInMap(properties, chartPrefix + "xAxisSortParameter", chart.getxAxisSortParameter(), report);
   }

   /**
    * Denormalizes Series transfer objects into report properties
    *
    * @param properties
    * @param chart
    * @param chartPrefix
    */
   private void storeSeries(Map<String, ReportProperty> properties, Chart chart, String chartPrefix, Report report) {
      if (chart.getSeriesList() == null) {
         return;
      }

      for (int i = 0; i < chart.getSeriesList().size(); i++) {
         Chart.Series series = chart.getSeriesList().get(i);
         String seriesPrefix = chartPrefix + "series" + i + ".";

         ReportUtils.createOrUpdateReportPropertyInMap(properties, seriesPrefix + "name", series.getName(), report);
         ReportUtils.createOrUpdateReportPropertyInMap(properties, seriesPrefix + "tags", series.getTags(), report);
         ReportUtils.createOrUpdateReportPropertyInMap(properties, seriesPrefix + "metricId", series.getMetricId().toString(), report);
      }
   }

   /**
    * Denormalizes Baseline transfer objects into report properties
    *
    * @param properties
    * @param chart
    * @param chartPrefix
    * @param report
    */
   private void storeBaselines(Map<String, ReportProperty> properties, Chart chart, String chartPrefix, Report report) {
      if (chart.getBaselines() == null) {
         return;
      }

      for (int i = 0; i < chart.getBaselines().size(); i++) {
         Chart.Baseline baseline = chart.getBaselines().get(i);
         String baselinePrefix = chartPrefix + "baseline" + i + ".";

         ReportUtils.createOrUpdateReportPropertyInMap(properties, baselinePrefix + "name", baseline.getName(), report);
         ReportUtils.createOrUpdateReportPropertyInMap(properties, baselinePrefix + "metricId", baseline.getMetricId().toString(), report);
         ReportUtils.createOrUpdateReportPropertyInMap(properties, baselinePrefix + "execId", baseline.getExecutionId().toString(), report);
      }
   }

   /**
    * Recreates all charts transfer objects from report properties
    *
    * @param properties report properties
    * @return
    */
   private List<Chart> loadTOFromReportProperties(Map<String, ReportProperty> properties) {
      List<Chart> charts = new ArrayList<>();

      int chartIndex = 0;
      while (properties.containsKey("chart" + chartIndex + ".name")) {
         Chart chart = new Chart();

         loadChartProperties(chartIndex, chart, properties);
         loadSeries(chartIndex, chart, properties);
         loadBaselines(chartIndex, chart, properties);

         charts.add(chart);
         chartIndex++;
      }

      return charts;
   }

   /**
    * Parses and adds top-level properties of single chart from report properties
    *
    * @param chartIndex index of the chart being processed
    * @param chart      chart transfer object to be modified
    * @param properties report properties
    */
   private void loadChartProperties(int chartIndex, Chart chart, Map<String, ReportProperty> properties) {
      chart.setName(properties.get("chart" + chartIndex + ".name").getValue());
      chart.setTestId(Long.parseLong(properties.get("chart" + chartIndex + ".test").getValue()));
      chart.setxAxisSort(Chart.AxisOption.valueOf(properties.get("chart" + chartIndex + ".xAxisSortType").getValue()));
      chart.setxAxisLabel(Chart.AxisOption.valueOf(properties.get("chart" + chartIndex + ".xAxisLabelType").getValue()));

      if (properties.containsKey("chart" + chartIndex + ".xAxisSortParameter")) {
         chart.setxAxisSortParameter(properties.get("chart" + chartIndex + ".xAxisSortParameter").getValue());
      }

      if (properties.containsKey("chart" + chartIndex + ".xAxisLabelParameter")) {
         chart.setxAxisLabelParameter(properties.get("chart" + chartIndex + ".xAxisLabelParameter").getValue());
      }
   }

   /**
    * Creates Series transfer objects from report properties for specified chart.
    *
    * @param chartIndex index of the chart being processed
    * @param chart      chart transfer object to be modified
    * @param properties report properties
    */
   private void loadSeries(int chartIndex, Chart chart, Map<String, ReportProperty> properties) {
      String seriesPrefix = "chart" + chartIndex + ".series";
      int seriesIndex = 0;

      while (properties.containsKey(seriesPrefix + seriesIndex + ".name")) {
         Chart.Series series = new Chart.Series();
         series.setName(properties.get(seriesPrefix + seriesIndex + ".name").getValue());
         series.setTags(properties.get(seriesPrefix + seriesIndex + ".tags").getValue());
         series.setMetricId(Long.parseLong(properties.get(seriesPrefix + seriesIndex + ".metricId").getValue()));

         if (chart.getSeriesList() == null) {
            chart.setSeriesList(new ArrayList<>());
         }

         chart.getSeriesList().add(series);
         seriesIndex++;
      }
   }

   /**
    * Creates Baseline transfer objects from report properties for specified chart.
    *
    * @param chartIndex index of the chart being processed
    * @param chart      chart transfer object to be modified
    * @param properties report properties
    */
   private void loadBaselines(int chartIndex, Chart chart, Map<String, ReportProperty> properties) {
      String baselinePrefix = "chart" + chartIndex + ".baseline";
      int baselineIndex = 0;

      while (properties.containsKey(baselinePrefix + baselineIndex + ".name")) {
         Chart.Baseline baseline = new Chart.Baseline();
         baseline.setName(properties.get(baselinePrefix + baselineIndex + ".name").getValue());
         baseline.setExecutionId(Long.parseLong(properties.get(baselinePrefix + baselineIndex + ".execId").getValue()));
         baseline.setMetricId(Long.parseLong(properties.get(baselinePrefix + baselineIndex + ".metricId").getValue()));

         if (chart.getBaselines() == null) {
            chart.setBaselines(new ArrayList<>());
         }

         chart.getBaselines().add(baseline);
         baselineIndex++;
      }
   }

   /**
    * Constructs simple DataPoint objects which represents a single boxplot in the chart. Therefore it gathers all
    * values of one test executions and group them together in a single object.
    *
    * @param resultWrappers results from database
    * @return list of datapoints ready to be printed in the chart
    */
   private List<Chart.Series.DataPoint> assembleResults(List<MultiValueResultWrapper> resultWrappers) {
      List<Chart.Series.DataPoint> seriesResults = new ArrayList<>();

      for (MultiValueResultWrapper resultWrapper : resultWrappers) {
         if (resultWrapper.getValues() == null || resultWrapper.getValues().isEmpty()) {
            continue;
         }

         String randomKeyOfMultiValue = resultWrapper.getValues().keySet().stream().findAny().get();
         List<Double> values = resultWrapper.getValues().get(randomKeyOfMultiValue).values().stream().collect(Collectors.toList());

         Chart.Series.DataPoint dataPoint = new Chart.Series.DataPoint(resultWrapper.getExecId(),
                                                                       resultWrapper.getIndexObject(),
                                                                       values);
         seriesResults.add(dataPoint);
      }

      return seriesResults;
   }

}
