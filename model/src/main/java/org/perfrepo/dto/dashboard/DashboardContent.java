package org.perfrepo.dto.dashboard;

import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;

import java.util.List;

/**
 * Data transfer object that represents content of the dashboard page.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class DashboardContent {

    private List<TestExecutionDto> latestTestExecutions;

    private List<ReportDto> favouriteReports;

    public List<TestExecutionDto> getLatestTestExecutions() {
        return latestTestExecutions;
    }

    public void setLatestTestExecutions(List<TestExecutionDto> latestTestExecutions) {
        this.latestTestExecutions = latestTestExecutions;
    }

    public List<ReportDto> getFavouriteReports() {
        return favouriteReports;
    }

    public void setFavouriteReports(List<ReportDto> favouriteReports) {
        this.favouriteReports = favouriteReports;
    }
}