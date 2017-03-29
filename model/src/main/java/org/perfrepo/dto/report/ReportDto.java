package org.perfrepo.dto.report;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.perfrepo.dto.report.box_plot.BoxPlotReportDto;
import org.perfrepo.dto.report.metric_history.MetricHistoryReportDto;
import org.perfrepo.dto.report.table_comparison.TableComparisonReportDto;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@JsonTypeInfo (
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TableComparisonReportDto.class, name = "TABLE_COMPARISON"),
        @JsonSubTypes.Type(value = MetricHistoryReportDto.class, name = "METRIC_HISTORY"),
        @JsonSubTypes.Type(value = BoxPlotReportDto.class, name = "BOX_PLOT")})
public abstract class ReportDto {

    private Long id;

    private String name;

    private String description;

    private String typeName;

    private List<PermissionDto> permissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }
}