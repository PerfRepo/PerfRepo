package org.perfrepo.web.adapter;

import org.perfrepo.dto.test_execution.TestExecutionDto;

import java.util.List;
import java.util.Set;

/**
 * Service adapter for test execution comparison session.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface ComparisonSessionAdapter {
    /**
     * Return selected test executions.
     *
     * @return List of selected test executions for comparison.
     */
    List<TestExecutionDto> getTestExecutions();

    /**
     * Add test executions to comparison session.
     *
     * @param testExecutionIds Set of test execution ids.
     *
     * @return List of selected test executions for comparison.
     */
    List<TestExecutionDto> addToComparison(Set<Long> testExecutionIds);

    /**
     * Remove test executions from comparison session.
     *
     * @param testExecutionIds Set of test execution ids.
     *
     * @return List of selected test executions for comparison.
     */
    List<TestExecutionDto> removeFromComparison(Set<Long> testExecutionIds);
}
