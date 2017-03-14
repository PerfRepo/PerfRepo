package org.perfrepo.dto.report.table_comparison;

import org.perfrepo.dto.report.ReportDto;

import java.util.List;

/**
 * Data transfer object for the table comparison report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TableComparisonReportDto extends ReportDto {

    private List<GroupDto> groups;

    public List<GroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDto> groups) {
        this.groups = groups;
    }
}