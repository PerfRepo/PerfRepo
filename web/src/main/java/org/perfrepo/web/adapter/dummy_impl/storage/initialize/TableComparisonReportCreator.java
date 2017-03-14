package org.perfrepo.web.adapter.dummy_impl.storage.initialize;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.table_comparison.*;
import org.perfrepo.dto.report.table_comparison.view.*;
import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.enums.report.CellStyle;
import org.perfrepo.enums.report.ComparisonItemSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TableComparisonReportCreator {

    private TableComparisonReportCreator() {

    }

    public static ReportDto createReport() {
        TableComparisonReportDto tableReport = new TableComparisonReportDto();
        tableReport.setName("Table comparison report WITH DATA");
        tableReport.setTypeName("Table comparison report");
        tableReport.setDescription("Description of <strong>table comparison</strong> report.");
        // group 1
        GroupDto group1 = new GroupDto();
        group1.setName("Comparison group");
        group1.setDescription("This is first group of <strong>table comparison table</strong>");
        group1.setThreshold(5);
        tableReport.setGroups(new ArrayList<>());
        tableReport.getGroups().add(group1);
        // table 1
        TableDto table1 = new TableDto();
        group1.setTables(new ArrayList<>());
        // add table to group twice
        group1.getTables().add(table1);
        group1.getTables().add(table1);
        table1.setName("Comparison table");
        table1.setDescription("Description of first table...");
        // table 1 - comparison items
        List<ComparisonItemDto> items1 = new ArrayList<>();
        table1.setItems(items1);
        items1.add(prepareComparisonItem("Test execution 1", 1L, null, false, null, null, ComparisonItemSelector.TEST_EXECUTION_ID));
        items1.add(prepareComparisonItem("Test execution 2", 2L, 1L, true, "echo AND test", null, ComparisonItemSelector.TAG_QUERY));
        items1.add(prepareComparisonItem("Test execution 3", 3L, 1L, false, "socket OR test", "version=1.2", ComparisonItemSelector.PARAMETER_QUERY));
        items1.add(prepareComparisonItem("Test execution 3", 4L, 2L, false, null, "version=1.2", ComparisonItemSelector.TEST_EXECUTION_ID));
        // table 1 - header cells
        List<HeaderCellDto> headerCells1 = new ArrayList<>();
        table1.setTableHeaderCells(headerCells1);
        headerCells1.add(prepareHeaderCell("Test execution 1", false, 1L));
        headerCells1.add(prepareHeaderCell("Test execution 2", true, 2L));
        headerCells1.add(prepareHeaderCell("Test execution 3", false, 3L));
        headerCells1.add(prepareHeaderCell("Test execution 4", false, 4L));
        // table 1 - rows
        List<RowDto> rows = new ArrayList<>();
        table1.setTableRows(rows);
        rows.add(prepareTableRow("Response time",
                prepareContentCell(false, CellStyle.BAD, 22.0, 10.0),
                prepareContentCell(true, CellStyle.NEUTRAL, 20.0, 0.0),
                prepareContentCell(false, CellStyle.BAD, 26.0, 30.55555),
                prepareContentCell(false, CellStyle.GOOD, 18.0, -10.1)));
        rows.add(prepareTableRow("Throughput",
                prepareContentCell(false, CellStyle.NEUTRAL, 15.45, 3.0),
                prepareContentCell(true, CellStyle.NEUTRAL, 15.0, 0.0),
                prepareContentCell(false, CellStyle.BAD, 14.25, -5.0),
                prepareContentCell(false, CellStyle.GOOD, 16.5, 10.0)));
        rows.add(prepareTableRowMultiValue());
        // add group to report twice
        tableReport.setGroups(new ArrayList<>());
        tableReport.getGroups().add(group1);
        tableReport.getGroups().add(group1);
        // permissions
        List<PermissionDto> permissions = new ArrayList<>();
        permissions.add(InstanceCreator.createPermission(AccessLevel.PUBLIC, AccessType.READ, null, null, null, null));
        permissions.add(InstanceCreator.createPermission(AccessLevel.GROUP, AccessType.WRITE, 1L, null,
                "super users", null));
        permissions.add(InstanceCreator.createPermission(AccessLevel.USER, AccessType.READ, null, 1L,
                null, "Jiri Grunwald (grunwjir)"));
        tableReport.setPermissions(permissions);
        return tableReport;
    }

    private static ComparisonItemDto prepareComparisonItem(String alias, Long executionId, Long testId, boolean baseline,
                                                    String tagQuery, String parameterQuery,
                                                    ComparisonItemSelector selector) {
        ComparisonItemDto item = new ComparisonItemDto();
        item.setAlias(alias);
        item.setExecutionId(executionId);
        item.setTestId(testId);
        item.setBaseline(baseline);
        item.setTagQuery(tagQuery);
        item.setParameterQuery(parameterQuery);
        item.setSelector(selector);
        return item;
    }

    private static HeaderCellDto prepareHeaderCell(String name, boolean baseline, Long testExecutionId) {
        HeaderCellDto headerCell = new HeaderCellDto();
        headerCell.setName(name);
        headerCell.setBaseline(baseline);
        headerCell.setTestExecutionId(testExecutionId);
        return  headerCell;
    }

    private static RowDto prepareTableRowMultiValue() {
        RowDto row = new RowDto();
        row.setMetricName("Response time multi-value");
        row.setValueType(MeasuredValueType.MULTI_VALUE);
        row.setCells(new ArrayList<>());
        row.getCells().add(prepareContentCellMultiValue(false, 20.24, 20.54, 21.43, 21.54, 34.43, 34.54, 34.98, 19.43, 18.98));
        row.getCells().add(prepareContentCellMultiValue(true, 19.14, 19.24, 20.12, 20.18, 24.43, 25.42, 26.18, 15.3, 13.88));
        row.getCells().add(prepareContentCellMultiValue(false, 21.46, 21.45, 22.39, 22.41, 35.32, 32.47, 31.81, 22.35, 20.18));
        row.getCells().add(prepareContentCellMultiValue(false, 22.41, 22.65, 22.14, 22.4, 28.31, 29.14, 30.98, 20.31, 17.98));
        return row;
    }

    private static ContentCellDto prepareContentCellMultiValue(boolean baseline, double...values) {
        List<MultiContentCellDto.ChartPointDto> chartValuesTime = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            MultiContentCellDto.ChartPointDto value = new MultiContentCellDto.ChartPointDto();
            value.setX(i + 5);
            value.setValue(values[i]);
            chartValuesTime.add(value);
        }

        List<MultiContentCellDto.ChartPointDto> chartValuesPercent = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            MultiContentCellDto.ChartPointDto value = new MultiContentCellDto.ChartPointDto();
            value.setX((i + 1) * (100.0 / values.length));
            value.setValue(values[i]);
            chartValuesPercent.add(value);
        }

        MultiContentCellDto contentCell = new MultiContentCellDto();
        contentCell.setBaseline(baseline);
        contentCell.setValues(new HashMap<>());

        contentCell.getValues().put("Time", chartValuesTime);
        contentCell.getValues().put("Percent", chartValuesPercent);

        return contentCell;
    }

    private static ContentCellDto prepareContentCell(boolean baseline, CellStyle style, double value, double valueByBaseline) {
        SingleContentCellDto contentCell = new SingleContentCellDto();
        contentCell.setBaseline(baseline);
        contentCell.setStyle(style);
        contentCell.setValue(value);
        contentCell.setValueDifferenceFromBaseline(valueByBaseline);
        return contentCell;
    }

    private static RowDto prepareTableRow(String metricName, ContentCellDto... cells) {
        RowDto row = new RowDto();
        row.setMetricName(metricName);
        row.setCells(Arrays.asList(cells));
        row.setValueType(MeasuredValueType.SINGLE_VALUE);
        return row;
    }
}