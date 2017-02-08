//package org.perfrepo.web.service.reports.metrichistory;
//
//import org.perfrepo.web.dao.TestDAO;
//import org.perfrepo.web.model.Metric;
//import org.perfrepo.web.model.Test;
//import org.perfrepo.web.model.to.MetricReportTO;
//
//import javax.inject.Inject;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * TODO: document this
// *
// * @author Jiri Holusa (jholusa@redhat.com)
// */
//public class MetricHistoryReportServiceBean {
//
//    @Inject
//    private TestDAO testDAO;
//
//    public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request) {
//        MetricReportTO.Response response = new MetricReportTO.Response();
//        for (MetricReportTO.ChartRequest chartRequest : request.getCharts()) {
//            MetricReportTO.ChartResponse chartResponse = new MetricReportTO.ChartResponse();
//            response.addChart(chartResponse);
//            if (chartRequest.getTestUid() == null) {
//                continue;
//            } else {
//                Test freshTest = testDAO.findByUid(chartRequest.getTestUid());
//                if (freshTest == null) {
//                    // test uid supplied but doesn't exist - pick another test
//                    response.setSelectionTests(testDAO.getAll());
//                    continue;
//                } else {
//                    freshTest = freshTest;
//                    chartResponse.setSelectedTest(freshTest);
//                    if (chartRequest.getSeries() == null || chartRequest.getSeries().isEmpty()) {
//                        continue;
//                    }
//                    for (MetricReportTO.SeriesRequest seriesRequest : chartRequest.getSeries()) {
//                        if (seriesRequest.getName() == null) {
//                            throw new IllegalArgumentException("series has null name");
//                        }
//                        MetricReportTO.SeriesResponse seriesResponse = new MetricReportTO.SeriesResponse(seriesRequest.getName());
//                        chartResponse.addSeries(seriesResponse);
//                        if (seriesRequest.getMetricName() == null) {
//                            continue;
//                        }
//                        Metric metric = freshTest.getMetrics().stream().filter(m -> m.getName().equals(seriesRequest.getMetricName())).findFirst().get();
//                        if (metric == null) {
//                            chartResponse.setSelectionMetrics(new ArrayList<>(freshTest.getMetrics()));
//                            continue;
//                        }
//                        seriesResponse.setSelectedMetric(metric);
//                        List<MetricReportTO.DataPoint> datapoints = null;
//                        //TODO: fix this
//                        //List<MetricReportTO.DataPoint> datapoints = testExecutionDAO.searchValues(freshTest.getId(), seriesRequest.getMetricName(), seriesRequest.getTags(), request.getLimitSize());
//                        if (datapoints.isEmpty()) {
//                            continue;
//                        }
//                        Collections.reverse(datapoints);
//                        seriesResponse.setDatapoints(datapoints);
//                    }
//
//                    for (MetricReportTO.BaselineRequest baselineRequest : chartRequest.getBaselines()) {
//                        if (baselineRequest.getName() == null) {
//                            throw new IllegalArgumentException("baseline has null name");
//                        }
//                        MetricReportTO.BaselineResponse baselineResponse = new MetricReportTO.BaselineResponse(baselineRequest.getName());
//                        chartResponse.addBaseline(baselineResponse);
//                        if (baselineRequest.getMetricName() == null) {
//                            continue;
//                        }
//                        Metric metric = freshTest.getMetrics().stream().filter(m -> m.getName().equals(baselineRequest.getMetricName())).findFirst().get();
//                        if (metric == null) {
//                            chartResponse.setSelectionMetrics(new ArrayList<>(freshTest.getMetrics()));
//                            continue;
//                        }
//                        baselineResponse.setSelectedMetric(metric);
//                        baselineResponse.setExecId(baselineRequest.getExecId());
//                        //TODO: fix this
//                        //baselineResponse.setValue(testExecutionDAO.getValueForMetric(baselineRequest.getExecId(), baselineRequest.getMetricName()));
//                    }
//                }
//            }
//        }
//        return response;
//    }
//}
