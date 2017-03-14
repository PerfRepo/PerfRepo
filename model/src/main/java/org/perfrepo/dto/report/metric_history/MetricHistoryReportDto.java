package org.perfrepo.dto.report.metric_history;

import org.perfrepo.dto.report.ReportDto;

import java.util.List;

/**
 * Data transfer object for the metric history report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricHistoryReportDto extends ReportDto {

    private List<ChartDto> charts;

    public List<ChartDto> getCharts() {
        return charts;
    }

    public void setCharts(List<ChartDto> charts) {
        this.charts = charts;
    }
}