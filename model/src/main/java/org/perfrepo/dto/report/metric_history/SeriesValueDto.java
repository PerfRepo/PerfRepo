package org.perfrepo.dto.report.metric_history;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class SeriesValueDto {

    private int x;

    private double y;

    private Long executionId;

    private String executionName;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }
}
