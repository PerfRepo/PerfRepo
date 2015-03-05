/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.controller.reports.metric;

import org.apache.log4j.Logger;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionParameter;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;
import org.perfrepo.model.to.MetricReportTO.BaselineRequest;
import org.perfrepo.model.to.MetricReportTO.BaselineResponse;
import org.perfrepo.model.to.MetricReportTO.ChartRequest;
import org.perfrepo.model.to.MetricReportTO.ChartResponse;
import org.perfrepo.model.to.MetricReportTO.DataPoint;
import org.perfrepo.model.to.MetricReportTO.Request;
import org.perfrepo.model.to.MetricReportTO.Response;
import org.perfrepo.model.to.MetricReportTO.SeriesRequest;
import org.perfrepo.model.to.MetricReportTO.SeriesResponse;
import org.perfrepo.model.user.User;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.controller.reports.ReportPermissionController;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.util.MessageUtils;
import org.perfrepo.web.util.ReportUtils;
import org.perfrepo.web.util.TagUtils;
import org.perfrepo.web.util.ViewUtils;
import org.perfrepo.web.viewscope.ViewScoped;
import org.richfaces.ui.output.chart.ChartDataModel.ChartType;
import org.richfaces.ui.output.chart.NumberChartDataModel;
import org.richfaces.ui.output.chart.PlotClickEvent;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Shows development of metric based on a specific test execution parameter.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named("metricReportBean")
@ViewScoped
public class MetricReportController extends BaseController {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(MetricReportController.class);
	private static final SimpleDateFormat DFMT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private static final String CHART_KEY_PREFIX = "chart";

	@Inject
	private TestService testService;

	@Inject
	private UserSession userSession;

	@Inject
	private ReportService reportService;

	@Inject
	private UserService userService;

	@Inject
	private ReportPermissionController reportAccessController;

	private User user;

	private Response report;
	private String reportName;

	private Long reportId;
	private List<ChartSpec> chartSpecs;
	private List<SeriesSpec> seriesSpecs;
	private List<BaselineSpec> baselineSpecs;
	private List<Test> selectionTests;
	private boolean configVisible;
	private PointDetails pointDetails;

	private static final Comparator<ChartSpec> SORT_BY_NAME = new Comparator<MetricReportController.ChartSpec>() {
		@Override
		public int compare(ChartSpec o1, ChartSpec o2) {
			return o1.getChartName().compareTo(o2.getChartName());
		}
	};

	@PostConstruct
	protected void init() {
		if (selectionTests == null) {
			selectionTests = testService.getAllTests();
			Collections.sort(selectionTests, new Comparator<Test>() {
				@Override
				public int compare(Test o1, Test o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}

		String reportIdParam = getRequestParam("reportId");
		Long reportId = reportIdParam != null ? Long.parseLong(reportIdParam) : null;
		if (reportId != null) {
			generateSavedReport(reportId);
		} else {
			configVisible = true;
			generateNewReport();
		}
	}

	public List<String> getChartNames() {
		List<String> r = new ArrayList<String>(chartSpecs.size());
		for (ChartSpec s : chartSpecs) {
			r.add(s.getChartName());
		}
		return r;
	}

	public String getLinkToReport() {
		if (reportId == null) {
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/reports/metric";
		} else {
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/reports/metric/" + reportId;
		}
	}

	/**
	 * called on preRenderView
	 */
	public void preRender() {
		reloadSessionMessages();
	}

	public void previewReport() {
		updateReport();
	}

	public void save() {
		updateReport();

		User user = userService.getUser(userSession.getUser().getId());
		Report report = new Report();

		if (reportId != null) { //editing existing report
			report = reportService.getFullReport(new Report(reportId));
		}

		report.setName(reportName);
		report.setType("Metric");
		report.setUser(user);

		Map<String, ReportProperty> reportProperties = report.getProperties();
		if (reportProperties == null) {
			reportProperties = new HashMap<String, ReportProperty>();
		}

		for (int i = 0; i < chartSpecs.size(); i++) {
			ChartSpec chart = chartSpecs.get(i);
			String chartPrefix = CHART_KEY_PREFIX + i;
			ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, chartPrefix + ".name", chart.getChartName(), report);
			ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, chartPrefix + ".test", Long.toString(chart.getSelectedTestId()), report);

			for (int j = 0; j < chart.chartSeries.size(); j++) {
				SeriesSpec series = chart.chartSeries.get(j);
				String seriesPrefix = chartPrefix + ".series" + j;
				ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, seriesPrefix + ".name", series.getName(), report);
				ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, seriesPrefix + ".metric", Long.toString(series.getSelectedMetricId()), report);
				ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, seriesPrefix + ".tags", series.getSelectedTags(), report);
			}

			for (int j = 0; j < chart.chartBaselines.size(); j++) {
				BaselineSpec baseline = chart.chartBaselines.get(j);
				String baselinePrefix = chartPrefix + ".baseline" + j;
				ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, baselinePrefix + ".name", baseline.getName(), report);
				ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, baselinePrefix + ".metric", Long.toString(baseline.getSelectedMetricId()), report);
				ReportUtils.createOrUpdateReportPropertyInMap(reportProperties, baselinePrefix + ".execId", Long.toString(baseline.getExecId()), report);
			}
		}

		report.setProperties(reportProperties);
		report.setPermissions(reportAccessController.getPermissions());
		if (reportId == null) {
			reportService.createReport(report);
		} else {
			reportService.updateReport(report);
		}

		addSessionMessage(INFO, "page.reports.metric.reportSaved");
		reloadSessionMessages();
	}

	public void addSeries() {
		if (chartSpecs == null || chartSpecs.isEmpty()) {
			addMessage(ERROR, "page.metricreport.noChartSpecs");
		} else {
			SeriesSpec newSeries = new SeriesSpec(chartSpecs.get(0), new SeriesRequest("Series " + (seriesSpecs.size() + 1)));
			chartSpecs.get(0).addSeries(newSeries);
			seriesSpecs.add(newSeries);
		}
	}

	public void addBaseline() {
		if (chartSpecs == null || chartSpecs.isEmpty()) {
			addMessage(ERROR, "page.metricreport.noChartSpecs");
		} else {
			BaselineSpec newBaseline = new BaselineSpec(chartSpecs.get(0), new BaselineRequest("Baseline " + (baselineSpecs.size() + 1)));
			chartSpecs.get(0).addBaseline(newBaseline);
			baselineSpecs.add(newBaseline);
		}
	}

	public void removeChart(ChartSpec chartToRemove) {
		if (chartSpecs == null || chartSpecs.isEmpty()) {
			log.warn("Strange. Removing a non-existent chart.");
			return;
		}
		if (chartSpecs.remove(chartToRemove)) {
			if (chartSpecs.isEmpty()) {
				seriesSpecs.clear();
			} else {
				ChartSpec thesurvivor = chartSpecs.get(0);
				for (SeriesSpec series : chartToRemove.chartSeries) {
					series.chart = thesurvivor;
				}
			}
		}
	}

	public void addChart() {
		ChartSpec newSpec = new ChartSpec("Chart " + (chartSpecs.size() + 1));
		newSpec.setSelectedTestId(selectionTests.get(0).getId());
		newSpec.changeMetricSelection();
		chartSpecs.add(newSpec);
	}

	public void removeSeries(SeriesSpec seriesToRemove) {
		if (seriesToRemove.chart != null) {
			seriesToRemove.chart.removeSeries(seriesToRemove);
		}
		seriesSpecs.remove(seriesToRemove);
	}

	public void removeBaseline(BaselineSpec baselineToRemove) {
		if (baselineToRemove.chart != null) {
			baselineToRemove.chart.removeBaseline(baselineToRemove);
		}
		baselineSpecs.remove(baselineToRemove);
	}

	public void updateTestExecutionFromDetail() {
		try {
			testService.updateTestExecution(pointDetails.exec);
		} catch (ServiceException ex) {
			addMessage(ex);
		}
	}

	private void generateNewReport() {
		if (selectionTests == null || selectionTests.isEmpty()) {
			addMessage(INFO, "page.metricreport.noTests");
		}

		reportName = MessageUtils.getMessage("page.metricreport.newReport");

		chartSpecs = new ArrayList<ChartSpec>();
		ChartSpec chart = new ChartSpec("Chart 1");
		chart.setSelectedTestId(selectionTests.get(0).getId());
		chart.changeMetricSelection();
		chartSpecs.add(chart);

		seriesSpecs = new ArrayList<SeriesSpec>();
		baselineSpecs = new ArrayList<BaselineSpec>();

		SeriesSpec series = new SeriesSpec(chart, new SeriesRequest("Series 1"));
		if (chart.getSelectionMetrics() != null && !chart.getSelectionMetrics().isEmpty()) {
			series.setSelectedMetricId(chart.getSelectionMetrics().get(0).getId());
		}

		chart.addSeries(series);
		seriesSpecs.add(series);

		updateReport();
	}

	private void generateSavedReport(Long reportId) {
		try {
			Report report = reportService.getFullReport(new Report(reportId));
			this.reportId = report.getId();
			this.reportName = report.getName();

			int chartIdx = 0;
			String chartPrefix = CHART_KEY_PREFIX + chartIdx;

			List<ChartSpec> savedChartSpecs = new ArrayList<ChartSpec>();
			Map<String, ReportProperty> reportProperties = report.getProperties();
			while (reportProperties.containsKey(chartPrefix + ".name")) {
				ChartSpec chart = new ChartSpec(reportProperties.get(chartPrefix + ".name").getValue());
				chart.setSelectedTestId(Long.valueOf(reportProperties.get(chartPrefix + ".test").getValue()));
				chart.changeMetricSelection();

				int j = 0;
				String seriesPrefix = chartPrefix + ".series" + j;
				while (reportProperties.containsKey(seriesPrefix + ".name")) {
					SeriesSpec series = new SeriesSpec(chart, new SeriesRequest(reportProperties.get(seriesPrefix + ".name").getValue()));
					series.setSelectedMetricId(Long.valueOf(reportProperties.get(seriesPrefix + ".metric").getValue()));
					series.setSelectedTags(reportProperties.get(seriesPrefix + ".tags").getValue());
					chart.addSeries(series);

					j++;
					seriesPrefix = chartPrefix + ".series" + j;
				}

				j = 0;
				String baselinePrefix = chartPrefix + ".baseline" + j;
				while (reportProperties.containsKey(baselinePrefix + ".name")) {
					BaselineSpec baseline = new BaselineSpec(chart, new BaselineRequest(reportProperties.get(baselinePrefix + ".name").getValue()));
					baseline.setSelectedMetricId(Long.valueOf(reportProperties.get(baselinePrefix + ".metric").getValue()));
					baseline.setExecId(Long.valueOf(reportProperties.get(baselinePrefix + ".execId").getValue()));
					chart.addBaseline(baseline);

					j++;
					baselinePrefix = chartPrefix + ".baseline" + j;
				}
				savedChartSpecs.add(chart);
				chartIdx++;
				chartPrefix = "chart" + chartIdx;
			}

			chartSpecs = savedChartSpecs;
			seriesSpecs = new ArrayList<SeriesSpec>();
			baselineSpecs = new ArrayList<BaselineSpec>();

			updateReport();
		} catch (Exception e) {
			log.error("problem with saved report", e);
			addMessage(ERROR, "page.metricreport.savedReportProblem");
		}
	}

	private void updateReport() {
		Collections.sort(chartSpecs, SORT_BY_NAME);
		Request request = new Request();
		for (ChartSpec chartSpec : chartSpecs) {
			request.addChart(chartSpec.getRequest());
		}

		seriesSpecs.clear();
		baselineSpecs.clear();
		report = reportService.computeMetricReport(request);

		Iterator<ChartResponse> chartResponses = report.getCharts().iterator();
		Iterator<ChartRequest> chartRequests = request.getCharts().iterator();
		if (report.getCharts().size() != request.getCharts().size()) {
			throw new IllegalStateException("expected " + request.getCharts().size() + " returned " + report.getCharts().size());
		}

		for (ChartSpec chartSpec : chartSpecs) {
			ChartResponse chartResponse = chartResponses.next();
			recomputeChartData(chartSpec, chartResponse);
			if (chartResponse.getSelectedTest() == null) {
				addMessage(INFO, "page.metricreport.selectTestUID", chartSpec.getChartName());
			}
		}
	}

	private String findSelectedTestUID(Long id) {
		for (Test t : selectionTests) {
			if (id.equals(t.getId())) {
				return t.getUid();
			}
		}
		return null;
	}

	private void recomputeChartData(ChartSpec chartSpec, ChartResponse response) {
		if (response == null || response.getSeries() == null || response.getSeries().isEmpty()) {
			chartSpec.getChartSeries().clear();
			return;
		}

		int maxDatapointCount = 2;
		for (SeriesResponse seriesResponse : response.getSeries()) {
			SeriesSpec seriesSpec = chartSpec.findSeries(seriesResponse.getName());
			if (seriesSpec == null) {
				throw new IllegalStateException("Series \"" + seriesResponse.getName() + "\" doesn't exist anymore.");
			}

			seriesSpec.clearModel();

			if (seriesResponse.getDatapoints() != null) {
				for (int i = 0; i < seriesResponse.getDatapoints().size(); i++) {
					DataPoint dp = seriesResponse.getDatapoints().get(i);
					seriesSpec.addPoint(i, dp.value, dp.execId);
				}

				if (seriesResponse.getDatapoints().size() > maxDatapointCount) {
					maxDatapointCount = seriesResponse.getDatapoints().size();
				}
			}

			seriesSpecs.add(seriesSpec);
		}

		for (BaselineResponse baselineResponse : response.getBaselines()) {
			BaselineSpec baselineSpec = chartSpec.findBaseline(baselineResponse.getName());
			if (baselineSpec == null) {
				throw new IllegalStateException("Baseline \"" + baselineResponse.getName() + "\" doesn't exist anymore.");
			}

			baselineSpec.clearModel();
			if (baselineResponse.getValue() != null) {
				baselineSpec.setValue(maxDatapointCount, baselineResponse.getValue(), baselineResponse.getExecId());
			}

			baselineSpecs.add(baselineSpec);
		}
	}

	private ChartSpec findChartSpecByName(String name) {
		for (ChartSpec s : chartSpecs) {
			if (name.equals(s.getChartName())) {
				return s;
			}
		}
		return null;
	}

	public List<ChartSpec> getChartSpecs() {
		return chartSpecs;
	}

	public List<SeriesSpec> getSeriesSpecs() {
		return seriesSpecs;
	}

	public List<BaselineSpec> getBaselineSpecs() {
		return baselineSpecs;
	}

	public List<Test> getSelectionTests() {
		return selectionTests;
	}

	public boolean isConfigVisible() {
		return configVisible;
	}

	public void showConfig() {
		this.configVisible = true;
	}

	public void hideConfig() {
		this.configVisible = false;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public PointDetails getPointDetails() {
		return pointDetails;
	}

	public String getDisplayValueFavParam(PointDetailsFavParam favParam) {
		return ViewUtils.displayValue(favParam.getParam());
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public void setPointDetails(PointDetails pointDetails) {
		this.pointDetails = pointDetails;
	}

	/** -----------------------------------------------------------------------------------
	 * Definitions of inner classes
	 ** -----------------------------------------------------------------------------------*/

	/**
	 * Specifies one chart in metric history report
	 */
	public class ChartSpec {

		private boolean renderDetails;
		private String chartName;
		private Long selectedTestId;
		private ChartRequest request;
		private List<SeriesSpec> chartSeries = new ArrayList<SeriesSpec>();
		private List<BaselineSpec> chartBaselines = new ArrayList<BaselineSpec>();
		private List<Metric> selectionMetrics;

		public ChartSpec(String chartName) {
			this.chartName = chartName;
			this.request = new ChartRequest();
		}

		public void setSelectedTestId(Long selectedTestId) {
			this.selectedTestId = selectedTestId;
			String testUid = findSelectedTestUID(this.selectedTestId);

			if (testUid == null) {
				throw new IllegalStateException("couldn't find test UID for id " + selectedTestId);
			}

			request.setTestUid(testUid);
		}

		public void addSeries(SeriesSpec spec) {
			chartSeries.add(spec);
			request.addSeries(spec.request);
		}

		public void removeSeries(SeriesSpec spec) {
			chartSeries.remove(spec);
			request.removeSeries(spec.request);
		}

		public void addBaseline(BaselineSpec spec) {
			chartBaselines.add(spec);
			request.addBaseline(spec.request);
		}

		public void removeBaseline(BaselineSpec spec) {
			chartBaselines.remove(spec);
			request.removeBaselines(spec.request);
		}

		// chart config line listener
		public void selectedTest() {
			changeMetricSelection();
		}

		// chart config line listener
		public void editedName() {
			int i = 2;
			String original = chartName;
			//check if chart with this name already exists
			while (findOtherSpec(chartName) != null) {
				chartName = original + (" " + i);
				i++;
			}
			if (!chartName.equals(original)) {
				addMessage(ERROR, "page.metricreport.chartExists", original);
			}
		}

		/**
		 * Listener for click event on chart to make details of execution visible
		 *
		 * @param event
		 */
		public void chartActionListener(PlotClickEvent event) {
			for (ChartSpec c : chartSpecs) {
				c.renderDetails = false;
			}

			renderDetails = true;
			if (event.getSeriesIndex() >= chartSeries.size() + chartBaselines.size()) {
				throw new IllegalStateException("series index " + event.getSeriesIndex() + " bigger than or equal to series list size " + chartSeries.size());
			}

			pointDetails = new PointDetails();

			if (event.getSeriesIndex() >= chartSeries.size()) {
				BaselineSpec baseline = chartBaselines.get(event.getSeriesIndex() - chartSeries.size());
				if (event.getPointIndex() >= 2) {
					throw new IllegalStateException("point index " + event.getPointIndex() + " too big in baseline series");
				}

				pointDetails.execId = baseline.getValueExecId();
			} else {
				SeriesSpec series = chartSeries.get(event.getSeriesIndex());
				if (event.getPointIndex() >= series.execIds.size()) {
					throw new IllegalStateException("point index " + event.getPointIndex() + " bigger than or equal to execId list size " + series.execIds.size());
				}

				pointDetails.execId = series.execIds.get(event.getPointIndex());
			}

			pointDetails.exec = testService.getFullTestExecution(pointDetails.execId);

			for (FavoriteParameter fp : userService.getFavoriteParametersForTest(pointDetails.exec.getTest())) {
				pointDetails.favParams.add(new PointDetailsFavParam(fp.getLabel(), pointDetails.exec.findParameter(fp.getParameterName())));
			}
		}

		/**
		 * Gets series object by name
		 *
		 * @param name
		 * @return Series object with corresponding name
		 */
		public SeriesSpec findSeries(String name) {
			for (SeriesSpec spec : this.chartSeries) {
				if (spec.getName().equals(name)) {
					return spec;
				}
			}

			return null;
		}

		/**
		 * Gets baseline object by name
		 *
		 * @param name
		 * @return Baseline object with corresponding name
		 */
		public BaselineSpec findBaseline(String name) {
			for (BaselineSpec spec : this.chartBaselines) {
				if (spec.getName().equals(name)) {
					return spec;
				}
			}

			return null;
		}

		/**
		 * Gets metrics name from id
		 *
		 * @param id
		 * @return Metric object with corresponding ID
		 */
		public String findMetricName(Long id) {
			if (id == null) {
				return null;
			}

			if (selectionMetrics == null) {
				return null;
			}

			for (Metric m : selectionMetrics) {
				if (id.equals(m.getId())) {
					return m.getName();
				}
			}

			return null;
		}

		/**
		 * When changed test of the chart, the metrics has to be updated. If metrics is not present in the newly
		 * selected chart, assign selected metric to series as the first metric in the list. Basically resets the
		 * selected metrics on chart change.
		 */
		private void changeMetricSelection() {
			if (selectedTestId == null) {
				throw new IllegalStateException("can't change metrics with null test ID");
			}

			selectionMetrics = testService.getAllMetrics(selectedTestId);
			if (selectionMetrics.isEmpty()) {
				addMessage(ERROR, "page.metricreport.noMetrics", findSelectedTestUID(selectedTestId));
			} else if (seriesSpecs != null) {
				for (SeriesSpec series : chartSeries) {
					if (series.getSelectedMetricId() == null || findMetricName(series.getSelectedMetricId()) == null) {
						series.setSelectedMetricId(selectionMetrics.get(0).getId());
					}
				}
			}
		}

		private ChartSpec findOtherSpec(String name) {
			for (ChartSpec chartSpec : chartSpecs) {
				if (chartSpec != this && chartSpec.chartName.equals(chartName)) {
					return chartSpec;
				}
			}

			return null;
		}

		public List<Metric> getSelectionMetrics() {
			return selectionMetrics;
		}

		public List<BaselineSpec> getChartBaselines() {
			return chartBaselines;
		}

		public boolean isRenderDetails() {
			return renderDetails;
		}

		public String getChartName() {
			return chartName;
		}

		public void setChartName(String chartName) {
			this.chartName = chartName;
		}

		public Long getSelectedTestId() {
			return selectedTestId;
		}

		public ChartRequest getRequest() {
			return request;
		}

		public List<SeriesSpec> getChartSeries() {
			return chartSeries;
		}
	}

	/**
	 * Specifies single series (single line) in chart
	 */
	public class SeriesSpec {

		private ChartSpec chart;
		private SeriesRequest request;
		private Long selectedMetricId;
		private String selectedTags;
		private NumberChartDataModel chartModel;
		private List<Long> execIds;

		private SeriesSpec(ChartSpec chart, SeriesRequest request) {
			super();

			this.chart = chart;
			this.request = request;
			this.chartModel = new NumberChartDataModel(ChartType.line);
			this.execIds = new ArrayList<Long>();
		}

		public NumberChartDataModel getChartModel() {
			return chartModel;
		}

		public void addPoint(double x, double y, long execId) {
			chartModel.put(x, y);
			execIds.add(execId);
		}

		public void clearModel() {
			this.chartModel = new NumberChartDataModel(ChartType.line);
			this.execIds.clear();
		}

		public void setChart(String chartName) {
			if (this.chart != null && chartName.equals(this.chart.chartName)) {
				return;
			}

			ChartSpec newSpec = findChartSpecByName(chartName);
			if (newSpec != null) {
				if (this.chart != null) {
					this.chart.removeSeries(this);
				}

				newSpec.addSeries(this);
				this.chart = newSpec;

				setSelectedMetricId(this.chart.selectionMetrics.isEmpty() ? null : this.chart.selectionMetrics.get(0).getId());
			} else {
				throw new IllegalArgumentException("can't find chart " + chartName);
			}
		}

		public String getName() {
			return request.getName();
		}

		public void setName(String name) {
			request.setName(name);
		}

		public Long getSelectedMetricId() {
			return selectedMetricId;
		}

		public void setSelectedMetricId(Long selectedMetricId) {
			this.selectedMetricId = selectedMetricId;
			request.setMetricName(chart.findMetricName(selectedMetricId));
		}

		public String getSelectedTags() {
			return selectedTags;
		}

		public void setSelectedTags(String selectedTags) {
			this.selectedTags = selectedTags;
			this.request.setTags(TagUtils.parseTags(selectedTags));
		}

		public String getMetricName() {
			return this.request.getMetricName();
		}

		public List<Metric> getSelectionMetrics() {
			return chart.getSelectionMetrics();
		}

		public String getChart() {
			return chart.getChartName();
		}

		@Override
		public String toString() {
			return super.toString() + "(metricId=" + selectedMetricId + ", selectedTags=" + selectedTags + ")";
		}
	}

	/**
	 * Specifies baseline (one line) in chart. Baseline is basically a series with only two points,
	 * so a horizontal line.
	 */
	public class BaselineSpec {

		private ChartSpec chart;
		private BaselineRequest request;
		private Long selectedMetricId;
		private NumberChartDataModel chartModel;
		private Long valueExecId;

		private BaselineSpec(ChartSpec chart, BaselineRequest request) {
			super();

			this.chart = chart;
			this.request = request;
			this.chartModel = new NumberChartDataModel(ChartType.line);
		}

		public NumberChartDataModel getChartModel() {
			return chartModel;
		}

		public void setValue(int maxDataPointCount, double value, Long execId) {
			chartModel.put(0, value);
			chartModel.put(maxDataPointCount - 1, value);
			this.valueExecId = execId;
		}

		public void clearModel() {
			this.chartModel = new NumberChartDataModel(ChartType.line);
			this.valueExecId = null;
		}

		public String getName() {
			return request.getName();
		}

		public void setName(String name) {
			request.setName(name);
		}

		public List<Metric> getSelectionMetrics() {
			return chart.getSelectionMetrics();
		}

		public void setChart(String chartName) {
			if (this.chart != null && chartName.equals(this.chart.chartName)) {
				return;
			}

			ChartSpec newSpec = findChartSpecByName(chartName);
			if (newSpec != null) {
				if (this.chart != null) {
					this.chart.removeBaseline(this);
				}

				newSpec.addBaseline(this);
				this.chart = newSpec;

				setSelectedMetricId(this.chart.selectionMetrics.isEmpty() ? null : this.chart.selectionMetrics.get(0).getId());
			} else {
				throw new IllegalArgumentException("can't find chart " + chartName);
			}
		}

		public String getChart() {
			return chart.getChartName();
		}

		public Long getSelectedMetricId() {
			return selectedMetricId;
		}

		public void setSelectedMetricId(Long selectedMetricId) {
			this.selectedMetricId = selectedMetricId;
			request.setMetricName(chart.findMetricName(selectedMetricId));
		}

		public String getMetricName() {
			return this.request.getMetricName();
		}

		public Long getExecId() {
			return request.getExecId();
		}

		public void setExecId(Long execId) {
			request.setExecId(execId);
		}

		public Long getValueExecId() {
			return valueExecId;
		}

		@Override
		public String toString() {
			return super.toString() + "(metricId=" + selectedMetricId + ", execId=" + (request == null ? null : request.getExecId()) + ")";
		}
	}

	/**
	 * Wrapper for displaying favorite parameter when clicking on chart showing test execution details.
	 * New panel appears and shows values of favorite parameters of the test selected by user.
	 */
	public class PointDetailsFavParam {

		private String label;
		private TestExecutionParameter param;

		private PointDetailsFavParam(String name, TestExecutionParameter param) {
			super();

			this.label = name;
			this.param = param;
		}

		public String getLabel() {
			return label;
		}

		public TestExecutionParameter getParam() {
			return param;
		}
	}

	/**
	 * Represents detail of test execution when clicking into chart.
	 */
	public class PointDetails {

		private Long execId;
		private TestExecution exec;
		private String comment;
		private List<PointDetailsFavParam> favParams = new ArrayList<MetricReportController.PointDetailsFavParam>();

		public Long getExecId() {
			return execId;
		}

		public String getExecName() {
			return exec == null ? "N/A" : exec.getName();
		}

		public String getExecStarted() {
			return exec == null ? "N/A" : DFMT.format(exec.getStarted());
		}

		public String getComment() {
			return exec == null ? "" : exec.getComment();
		}

		public void setComment(String comment) {
			exec.setComment(comment);
		}

		public List<String> getTags() {
			return exec == null ? null : exec.getSortedTags();
		}

		public List<PointDetailsFavParam> getFavParams() {
			return favParams;
		}
	}
}