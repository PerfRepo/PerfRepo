package org.perfrepo.dto.report.table_comparison.view;

import java.util.List;
import java.util.Map;

/**
 * Represents cell in the comparison table for multi-value data.
 * This object is used for view of the report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MultiContentCellDto extends ContentCellDto {

    private Map<String, List<ChartPointDto>> values;

    public Map<String, List<ChartPointDto>> getValues() {
        return values;
    }

    public void setValues(Map<String, List<ChartPointDto>> values) {
        this.values = values;
    }

    public static class ChartPointDto {

        private double x;

        private double value;

        private double valueDifferenceFromBaseline;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getValueDifferenceFromBaseline() {
            return valueDifferenceFromBaseline;
        }

        public void setValueDifferenceFromBaseline(double valueDifferenceFromBaseline) {
            this.valueDifferenceFromBaseline = valueDifferenceFromBaseline;
        }
    }
}