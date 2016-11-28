package org.perfrepo.dto.test;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.user.GroupDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Data transfer object for {@link org.perfrepo.model.Test} entity that represents a test.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestDto {

    private Long id;

    @NotNull(message = "{page.test.nameRequired}")
    @Size(max = 2047)
    private String name;

    @NotNull(message = "{page.test.uidRequired}")
    @Size(max = 2047)
    private String uid;

    @Size(max = 10239)
    private String description;

    private GroupDto group;

    private List<MetricDto> metrics;

    private List<AlertDto> alerts;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupDto getGroup() {
        return group;
    }

    public void setGroup(GroupDto group) {
        this.group = group;
    }

    public List<MetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricDto> metrics) {
        this.metrics = metrics;
    }

    public List<AlertDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertDto> alerts) {
        this.alerts = alerts;
    }
}
