package org.perfrepo.web.service.reports.tablecomparison;

import org.perfrepo.web.service.reports.tablecomparison.model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TableComparisonReport {

    private String name;
    private String description;
    private List<Table> tables = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "TableComparisonReport{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", tables=" + tables +
                '}';
    }
}
