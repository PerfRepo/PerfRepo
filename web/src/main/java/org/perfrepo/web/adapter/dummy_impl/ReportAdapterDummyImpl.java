package org.perfrepo.web.adapter.dummy_impl;

import com.google.common.collect.Sets;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.report.metric_history.BaselineDto;
import org.perfrepo.dto.report.metric_history.ChartDto;
import org.perfrepo.dto.report.metric_history.MetricHistoryReportDto;
import org.perfrepo.dto.report.metric_history.SeriesDto;
import org.perfrepo.dto.report.table_comparison.ComparisonItemDto;
import org.perfrepo.dto.report.table_comparison.GroupDto;
import org.perfrepo.dto.report.table_comparison.TableComparisonReportDto;
import org.perfrepo.dto.report.table_comparison.TableDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.report.ComparisonItemSelector;
import org.perfrepo.web.adapter.ReportAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;


import javax.inject.Inject;
import java.util.List;

public class ReportAdapterDummyImpl implements ReportAdapter {

    @Inject
    private Storage storage;

    @Override
    public ReportDto getReport(Long id) {
        ReportDto report = storage.report().getById(id);
        if (report == null) {
            throw new NotFoundException("Report does not exist.");
        }

        return report;
    }

    @Override
    public ReportDto createReport(ReportDto report) {
        return storage.report().create(report);
    }

    @Override
    public ReportDto updateReport(ReportDto report) {
        return storage.report().update(report);
    }

    @Override
    public void removeReport(Long id) {
        storage.report().delete(id);
    }

    @Override
    public List<ReportDto> getAllReports() {
        return storage.report().getAll();
    }

    @Override
    public SearchResult<ReportDto> searchReports(ReportSearchCriteria searchParams) {
        return storage.report().search(searchParams);
    }

    @Override
    public void validateWizardReportInfoStep(ReportDto report) {
        ValidationErrors validation = new ValidationErrors();

        // test name
        if (report.getName() == null) {
            validation.addFieldError("name", "Report name is a required field.");
        } else if (report.getName().trim().length() < 3) {
            validation.addFieldError("name", "Report name must be at least three characters.");
        }

        // test description
        if (report.getDescription() != null && report.getDescription().length() > 500) {
            validation.addFieldError("description", "Report description must not be more than 500 characters.");
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Report contains validation errors, please fix it.", validation);
        }
    }

    @Override
    public void validateWizardReportConfigurationStep(ReportDto report) {
        if (report instanceof TableComparisonReportDto) {
            validateTableComparisonConfiguration((TableComparisonReportDto) report);
        }

        if (report instanceof MetricHistoryReportDto) {
            validateMetricHistoryReportConfiguration((MetricHistoryReportDto) report);
        }

    }

    @Override
    public void validateWizardReportPermissionStep(ReportDto report) {
        ValidationErrors validation = new ValidationErrors();

        if (report.getPermissions() == null || report.getPermissions().size() == 0) {
            validation.addFormError("permissions", "No permission!");
        } else {
            for (int permissionIndex = 0; permissionIndex < report.getPermissions().size(); permissionIndex++) {
                PermissionDto permission = report.getPermissions().get(permissionIndex);
                if (permission.getLevel() == null) {
                    validation.addFieldError("permissions[" + permissionIndex + "].level", "Please select value.");
                } else if (permission.getLevel().equals(AccessLevel.GROUP) && permission.getGroupId() == null) {
                    validation.addFieldError("permissions[" + permissionIndex + "].groupId", "Please select group.");
                } else if (permission.getLevel().equals(AccessLevel.USER) && permission.getUserId() == null)
                    validation.addFieldError("permissions[" + permissionIndex + "].userId", "Please select user.");
                if (permission.getLevel() != null && permission.getType() == null) {
                    validation.addFieldError("permissions[" + permissionIndex + "].type", "Please select value.");
                }
            }
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Permissions contain validation errors, please fix it.", validation);
        }
    }

    private void validateMetricHistoryReportConfiguration(MetricHistoryReportDto report) {
        ValidationErrors validation = new ValidationErrors();

        if (report.getCharts() == null || report.getCharts().isEmpty()) {
            validation.addFormError("charts", "No chart!");
        } else {
            for (int chartIndex = 0; chartIndex < report.getCharts().size(); chartIndex++) {
                ChartDto chart = report.getCharts().get(chartIndex);
                // chart name
                if (chart.getName() == null) {
                    validation.addFieldError("charts[" + chartIndex + "].name", "Chart name is a required field.");
                } else if (chart.getName().trim().length() < 3) {
                    validation.addFieldError("charts[" + chartIndex + "].name", "Chart name must be at least three characters.");
                }
                // chart description
                if (chart.getDescription() != null && chart.getDescription().length() > 500) {
                    validation.addFieldError("charts[" + chartIndex + "].description", "Chart description must not be more than 500 characters.");
                }
                // series
                if (chart.getSeries() == null || chart.getSeries().isEmpty()) {
                    validation.addFormError("charts[" + chartIndex + "].series", "No series!");
                } else {
                    validateSeries(validation, chart, chartIndex);
                }
                // baselines
                if (chart.getBaselines() == null || chart.getBaselines().isEmpty()) {
                    validation.addFormError("charts[" + chartIndex + "].baselines", "No baseline!");
                } else {
                    validateBaselines(validation, chart, chartIndex);
                }
            }
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Report contains validation errors, please fix it.", validation);
        }

    }

    private void validateTableComparisonConfiguration(TableComparisonReportDto report) {
        ValidationErrors validation = new ValidationErrors();

        if (report.getGroups() == null || report.getGroups().isEmpty()) {
            validation.addFormError("groups", "No group!");
        } else {
            for (int groupIndex = 0; groupIndex < report.getGroups().size(); groupIndex++) {
                GroupDto group = report.getGroups().get(groupIndex);
                // group name
                if (group.getName() == null) {
                    validation.addFieldError("groups[" + groupIndex + "].name", "Group name is a required field.");
                } else if (group.getName().trim().length() < 3) {
                    validation.addFieldError("groups[" + groupIndex + "].name", "Group name must be at least three characters.");
                }
                // group description
                if (group.getDescription() != null && group.getDescription().length() > 500) {
                    validation.addFieldError("groups[" + groupIndex + "].description", "Group description must not be more than 500 characters.");
                }
                // group threshold
                if (group.getThreshold() <= 0) {
                    validation.addFieldError("groups[" + groupIndex + "].threshold", "Group threshold must be greater than zero.");
                }
                // tables
                if (group.getTables() == null || group.getTables().isEmpty()) {
                    validation.addFormError("groups[" + groupIndex + "].tables", "No table comparison!");
                    continue;
                }
                // validate tables
                validateTables(validation, group, groupIndex);
            }
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Report contains validation errors, please fix it.", validation);
        }
    }

    private void validateSeries(ValidationErrors validation, ChartDto chart, int chartIndex) {
        for (int seriesIndex = 0; seriesIndex < chart.getSeries().size(); seriesIndex++) {
            SeriesDto series = chart.getSeries().get(seriesIndex);
            // series name
            if (series.getName() == null) {
                validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].name", "Series name is a required field");
            } else if (series.getName().trim().length() < 3) {
                validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].name", "Series name must be at least three characters.");
            }
            // series test
            TestDto test = storage.test().getById(series.getTestId());
            if (test == null) {
                validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].testId", "Test not found.");
            } else {
                // series metric
                MetricDto metric = storage.metric().getById(series.getMetricId());
                if (metric == null) {
                    validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].metricId", "Metric not found.");
                } else {
                    if (!test.getMetrics().contains(metric)) {
                        validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].metricId", "Metric not found.");
                    }
                }
            }
            // series filter
            if (series.getFilter() == null) {
                validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].filter", "Not selected.");
            } else {
                // series tag query
                if (series.getFilter().equals(ComparisonItemSelector.TAG_QUERY) && (series.getTagQuery() == null || series.getTagQuery().isEmpty())) {
                    validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].tagQuery", "Bad tag query.");
                }
                // series parameter query
                if (series.getFilter().equals(ComparisonItemSelector.PARAMETER_QUERY) && (series.getParameterQuery() == null || series.getParameterQuery().isEmpty())) {
                    validation.addFieldError("charts[" + chartIndex + "].series[" + seriesIndex + "].parameterQuery", "Bad parameter query.");
                }
            }
        }
    }

    private void validateBaselines(ValidationErrors validation, ChartDto chart, int chartIndex) {
        for (int baselineIndex = 0; baselineIndex < chart.getBaselines().size(); baselineIndex++) {
            BaselineDto baseline = chart.getBaselines().get(baselineIndex);
            // baseline name
            if (baseline.getName() == null) {
                validation.addFieldError("charts[" + chartIndex + "].baselines[" + baselineIndex + "].name", "Baseline name is a required field");
            } else if (baseline.getName().trim().length() < 3) {
                validation.addFieldError("charts[" + chartIndex + "].baselines[" + baselineIndex + "].name", "Baseline name must be at least three characters.");
            }
            // baseline test execution
            TestExecutionDto testExecution = storage.testExecution().getById(baseline.getExecutionId());
            if (testExecution == null) {
                validation.addFieldError("charts[" + chartIndex + "].baselines[" + baselineIndex + "].executionId", "Test execution not found.");
            } else {
                // baseline metric
                MetricDto metric = storage.metric().getById(baseline.getMetricId());
                if (metric == null) {
                    validation.addFieldError("charts[" + chartIndex + "].baselines[" + baselineIndex + "].metricId", "Metric not found.");
                } else {
                    if (!testExecution.getTest().getMetrics().contains(metric)) {
                        validation.addFieldError("charts[" + chartIndex + "].baselines[" + baselineIndex + "].metricId", "Metric not found.");
                    }
                }
            }
        }
    }

    private void validateTables(ValidationErrors validation, GroupDto group, int groupIndex) {
        for (int tableIndex = 0; tableIndex < group.getTables().size(); tableIndex++) {
            TableDto table = group.getTables().get(tableIndex);
            // table name
            if (table.getName() == null) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].name", "Table name is a required field");
            } else if (table.getName().trim().length() < 3) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].name", "Table name must be at least three characters.");
            }
            // table description
            if (table.getDescription() != null && table.getDescription().length() > 500) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].description", "Table description must not be more than 500 characters.");
            }
            // items
            if (table.getItems() == null || table.getItems().isEmpty()) {
                validation.addFormError("groups[" + groupIndex + "].tables[" + tableIndex + "].items", "No item!");
                continue;
            }
            // validate items
            validateItems(validation, table, groupIndex, tableIndex);
        }
    }

    private void validateItems(ValidationErrors validation, TableDto table, int groupIndex, int tableIndex) {
        boolean baselineSelected = false;
        for (int itemIndex = 0; itemIndex < table.getItems().size(); itemIndex++) {
            ComparisonItemDto item = table.getItems().get(itemIndex);
            // item alias
            if (item.getAlias() == null) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].alias", "Item alias is a required field.");
            } else if (item.getAlias().trim().length() < 3) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].alias", "Item alias must be at least three characters.");
            }
            //baseline
            if (item.isBaseline()) {
                baselineSelected = true;
            }

            // item selector
            if (item.getSelector() == null) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].selector", "Item selector is required.");
            } else {
                switch (item.getSelector()) {
                    case TEST_EXECUTION_ID:
                        if (storage.testExecution().getById(item.getExecutionId()) == null) {
                            validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].executionId", "Test execution not found.");
                        }
                        break;
                    case TAG_QUERY:
                        if (storage.test().getById(item.getTestId()) == null) {
                            validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].testId", "Test not found.");
                        } else {
                            TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
                            criteria.setTestUIDsFilter(Sets.newHashSet(storage.test().getById(item.getTestId()).getUid()));
                            criteria.setTagQueriesFilter(Sets.newHashSet(item.getTagQuery()));
                            if (storage.testExecution().search(criteria).getTotalCount() == 0) {
                                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].tagQuery", "Test execution not found.");
                            }
                        }
                        break;
                    case PARAMETER_QUERY:
                        if (storage.test().getById(item.getTestId()) == null) {
                            validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].testId", "Test not found.");
                        } else {
                            TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
                            criteria.setTestUIDsFilter(Sets.newHashSet(storage.test().getById(item.getTestId()).getUid()));
                            criteria.setParameterQueriesFilter(Sets.newHashSet(item.getParameterQuery()));
                            if (storage.testExecution().search(criteria).getTotalCount() == 0) {
                                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].parameterQuery", "Test execution not found.");
                            }
                        }
                        break;
                }
            }

            if (!baselineSelected) {
                validation.addFormError("groups[" + groupIndex + "].tables[" + tableIndex + "].baseline", "No baseline selected.");
            }
        }
    }
}