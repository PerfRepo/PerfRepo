package org.perfrepo.dto.test_execution;

import org.perfrepo.enums.MeasuredValueType;

import java.util.List;
import java.util.Set;

/**
 * Represents group of test execution values. The value group is for each test metric.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValuesGroupDto {

    private String metricName;

    private Set<String> parameterNames;

    private List<ValueDto> values;

    private MeasuredValueType valueType;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Set<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(Set<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public List<ValueDto> getValues() {
        return values;
    }

    public void setValues(List<ValueDto> values) {
        this.values = values;
    }

    public MeasuredValueType getValueType() {
        return valueType;
    }

    public void setValueType(MeasuredValueType valueType) {
        this.valueType = valueType;
    }
}