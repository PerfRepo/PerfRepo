package org.perfrepo.dto.report.table_comparison;

import org.perfrepo.dto.report.table_comparison.view.HeaderCellDto;
import org.perfrepo.dto.report.table_comparison.view.RowDto;

import java.util.List;

/**
 * Data transfer object for the report. Represents one comparison table.
 * This object is used for view and configuration of the report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TableDto {

    private String name;

    private String description;

    /** Property is used for configuration of report. */
    private List<ComparisonItemDto> items;

    /** Property is used for view of report. */
    private List<HeaderCellDto> tableHeaderCells;

    /** Property is used for view of report. */
    private List<RowDto> tableRows;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ComparisonItemDto> getItems() {
        return items;
    }

    public void setItems(List<ComparisonItemDto> items) {
        this.items = items;
    }

    public List<HeaderCellDto> getTableHeaderCells() {
        return tableHeaderCells;
    }

    public void setTableHeaderCells(List<HeaderCellDto> tableHeaderCells) {
        this.tableHeaderCells = tableHeaderCells;
    }

    public List<RowDto> getTableRows() {
        return tableRows;
    }

    public void setTableRows(List<RowDto> tableRows) {
        this.tableRows = tableRows;
    }
}