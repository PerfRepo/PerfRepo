package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.web.adapter.ComparisonSessionAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ComparisonSessionAdapterDummyImpl implements ComparisonSessionAdapter {

    @Inject
    private Storage storage;

    @Override
    public List<TestExecutionDto> getTestExecutions() {
        return storage.getComparisonSession();
    }

    @Override
    public List<TestExecutionDto> addToComparison(Set<Long> testExecutionIds) {
        List<TestExecutionDto> testExecutions = testExecutionIds
                .stream()
                .map(testExecutionId -> storage.testExecution().getById(testExecutionId))
                .filter(testExecution -> !storage.getComparisonSession().contains(testExecution))
                .collect(Collectors.toList());

        storage.getComparisonSession().addAll(testExecutions);

        return  storage.getComparisonSession();
    }

    @Override
    public List<TestExecutionDto> removeFromComparison(Set<Long> testExecutionIds) {
        List<TestExecutionDto> testExecutions = testExecutionIds
                .stream()
                .map(testExecutionId -> storage.testExecution().getById(testExecutionId))
                .collect(Collectors.toList());

        storage.getComparisonSession().removeAll(testExecutions);

        return storage.getComparisonSession();
    }
}
