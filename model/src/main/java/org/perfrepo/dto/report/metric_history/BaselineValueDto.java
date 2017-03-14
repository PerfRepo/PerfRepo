package org.perfrepo.dto.report.metric_history;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BaselineValueDto {

    private int x1;

    private int x2;

    private double y;

    private Long executionId;

    private String executionName;

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
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
