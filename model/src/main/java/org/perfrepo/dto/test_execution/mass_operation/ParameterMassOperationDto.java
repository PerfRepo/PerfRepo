package org.perfrepo.dto.test_execution.mass_operation;

import org.perfrepo.dto.test_execution.ParameterDto;

import java.util.Set;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ParameterMassOperationDto {

    private ParameterDto parameter;

    private Set<Long> testExecutionIds;

    public ParameterDto getParameter() {
        return parameter;
    }

    public void setParameter(ParameterDto parameter) {
        this.parameter = parameter;
    }

    public Set<Long> getTestExecutionIds() {
        return testExecutionIds;
    }

    public void setTestExecutionIds(Set<Long> testExecutionIds) {
        this.testExecutionIds = testExecutionIds;
    }
}