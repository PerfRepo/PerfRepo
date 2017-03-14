package org.perfrepo.dto.report.table_comparison;

import java.util.List;

/**
 * Data transfer object for the table comparison report. The report can contains a few groups of comparison tables.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class GroupDto {

    private String name;

    private String description;

    private int threshold;

    private List<TableDto> tables;

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

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public List<TableDto> getTables() {
        return tables;
    }

    public void setTables(List<TableDto> tables) {
        this.tables = tables;
    }
}