package org.perfrepo.dto.report.table_comparison.view;

import org.perfrepo.enums.MeasuredValueType;

import java.util.List;

/**
 * Data transfer object for the table comparison report. Represents one row in the comparison table.
 * This object is used for view of the report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class RowDto {

    private String metricName;

    private MeasuredValueType valueType;

    private List<ContentCellDto> cells;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public MeasuredValueType getValueType() {
        return valueType;
    }

    public void setValueType(MeasuredValueType valueType) {
        this.valueType = valueType;
    }

    public List<ContentCellDto> getCells() {
        return cells;
    }

    public void setCells(List<ContentCellDto> cells) {
        this.cells = cells;
    }
}