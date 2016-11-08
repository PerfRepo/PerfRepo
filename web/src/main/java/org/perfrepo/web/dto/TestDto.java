package org.perfrepo.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

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

    @NotNull(message = "{page.test.groupRequired}")
    @Size(max = 255)
    private String groupId;

    private Collection<MetricDto> metrics;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Collection<MetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(Collection<MetricDto> metrics) {
        this.metrics = metrics;
    }
}
