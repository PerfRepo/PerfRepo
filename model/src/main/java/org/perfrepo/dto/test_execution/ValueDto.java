package org.perfrepo.dto.test_execution;

import java.util.Set;

/**
 * Represents one value of test execution {@link TestExecutionDto}.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueDto {

    private double value;

    private Set<ValueParameterDto> parameters;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Set<ValueParameterDto> getParameters() {
        return parameters;
    }

    public void setParameters(Set<ValueParameterDto> parameters) {
        this.parameters = parameters;
    }
}