package org.perfrepo.dto.report.box_plot;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BoxPlotSeriesValueDto {

    private double lowerQuartile;

    private double median;

    private double upperQuartile;

    private double lowerExtreme;

    private double upperExtreme;

    private List<Double> outliers;

    private Long executionId;

    private String executionName;

    private String label;

    public double getLowerQuartile() {
        return lowerQuartile;
    }

    public void setLowerQuartile(double lowerQuartile) {
        this.lowerQuartile = lowerQuartile;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getUpperQuartile() {
        return upperQuartile;
    }

    public void setUpperQuartile(double upperQuartile) {
        this.upperQuartile = upperQuartile;
    }

    public double getLowerExtreme() {
        return lowerExtreme;
    }

    public void setLowerExtreme(double lowerExtreme) {
        this.lowerExtreme = lowerExtreme;
    }

    public double getUpperExtreme() {
        return upperExtreme;
    }

    public void setUpperExtreme(double upperExtreme) {
        this.upperExtreme = upperExtreme;
    }

    public List<Double> getOutliers() {
        return outliers;
    }

    public void setOutliers(List<Double> outliers) {
        this.outliers = outliers;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}