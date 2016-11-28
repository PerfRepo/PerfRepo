package org.perfrepo.dto.metric;

/**
 * Data transfer object for {@link org.perfrepo.model.Metric} that represents a test metric.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricDto {

    private Long id;

    private String name;

    private String description;

    private String comparator;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }
}
