package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.dashboard.DashboardContent;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.adapter.DashboardAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.stream.Collectors;

/**
 * Temporary implementation of {@link DashboardAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class DashboardAdapterDummyImpl implements DashboardAdapter {

    @Inject
    private Storage storage;

    @Override
    public DashboardContent getDashboardContent() {
        DashboardContent content = new DashboardContent();

        // filter and set favourite reports
        content.setFavouriteReports(storage.report().getAll()
                .stream()
                .filter(report -> report.isFavourite())
                .collect(Collectors.toList()));

        // filter and set latest test executions
        TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
        criteria.setLimit(6);
        criteria.setOrderBy(OrderBy.DATE_DESC);
        content.setLatestTestExecutions(storage.testExecution().search(criteria).getData());

        return content;
    }
}