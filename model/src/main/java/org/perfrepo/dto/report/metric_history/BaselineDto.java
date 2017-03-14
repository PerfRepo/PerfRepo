package org.perfrepo.dto.report.metric_history;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BaselineDto {

    private String name;

    private Long metricId;

    private Long executionId;

    private BaselineValueDto value;

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

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public BaselineValueDto getValue() {
        return value;
    }

    public void setValue(BaselineValueDto value) {
        this.value = value;
    }
}
