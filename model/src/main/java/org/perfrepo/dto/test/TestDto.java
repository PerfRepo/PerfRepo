package org.perfrepo.dto.test;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.group.GroupDto;

import java.util.Set;

/**
 * Data transfer object that represents a test definition.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestDto {

    private Long id;

    private String name;

    private String uid;

    private String description;

    private GroupDto group;

    private Set<MetricDto> metrics;

    private Set<AlertDto> alerts;

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

    public Set<MetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<MetricDto> metrics) {
        this.metrics = metrics;
    }

    public Set<AlertDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(Set<AlertDto> alerts) {
        this.alerts = alerts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestDto)) return false;

        TestDto test = (TestDto) o;

        return getUid() != null ? getUid().equals(test.getUid()) : test.getUid() == null;
    }

    @Override
    public int hashCode() {
        return getUid() != null ? getUid().hashCode() : 0;
    }
}
