package org.perfrepo.dto.alert;

import org.perfrepo.dto.metric.MetricDto;

import java.util.Set;

/**
 * Data transfer object that represents a alert condition defined on a test.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AlertDto {

    private Long id;

    private String name;

    private String description;

    private String condition;

    private Set<String> links;

    private MetricDto metric;

    private Set<String> tags;

    private Long testId;

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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
        this.links = links;
    }

    public MetricDto getMetric() {
        return metric;
    }

    public void setMetric(MetricDto metric) {
        this.metric = metric;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertDto)) return false;

        AlertDto alert = (AlertDto) o;

        if (getName() != null ? !getName().equals(alert.getName()) : alert.getName() != null) return false;
        return getCondition() != null ? getCondition().equals(alert.getCondition()) : alert.getCondition() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getCondition() != null ? getCondition().hashCode() : 0);
        return result;
    }
}
