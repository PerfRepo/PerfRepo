package org.perfrepo.dto.report.metric_history;

import org.perfrepo.enums.report.ComparisonItemSelector;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class SeriesDto {

    private String name;

    private Long metricId;

    private Long testId;

    private String tagQuery;

    private String parameterQuery;

    private ComparisonItemSelector filter;

    private List<SeriesValueDto> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTagQuery() {
        return tagQuery;
    }

    public void setTagQuery(String tagQuery) {
        this.tagQuery = tagQuery;
    }

    public String getParameterQuery() {
        return parameterQuery;
    }

    public void setParameterQuery(String parameterQuery) {
        this.parameterQuery = parameterQuery;
    }

    public ComparisonItemSelector getFilter() {
        return filter;
    }

    public void setFilter(ComparisonItemSelector selector) {
        this.filter = selector;
    }

    public List<SeriesValueDto> getValues() {
        return values;
    }

    public void setValues(List<SeriesValueDto> values) {
        this.values = values;
    }
}
