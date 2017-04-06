package org.perfrepo.dto.report.metric_history;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BaselineDto {

    private String name;

    private String metricName;

    private Long executionId;

    private BaselineValueDto value;

    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
