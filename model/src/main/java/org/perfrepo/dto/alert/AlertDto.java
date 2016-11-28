package org.perfrepo.dto.alert;

import org.perfrepo.dto.metric.MetricDto;

import java.util.List;

/**
 * Data transfer object for {@link org.perfrepo.model.Alert} that represents a alert condition defined on a test.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AlertDto {

    private Long id;

    private String name;

    private String description;

    private String condition;

    private String links;

    private MetricDto metric;

    private List<String> tags;

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

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public MetricDto getMetric() {
        return metric;
    }

    public void setMetric(MetricDto metric) {
        this.metric = metric;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
