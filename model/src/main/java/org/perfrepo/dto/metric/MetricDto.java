package org.perfrepo.dto.metric;


import org.perfrepo.enums.MetricComparator;

/**
 * Data transfer object that represents a test metric.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricDto {

    private Long id;

    private String name;

    private String description;

    private MetricComparator comparator;

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

    public MetricComparator getComparator() {
        return comparator;
    }

    public void setComparator(MetricComparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricDto)) return false;

        MetricDto metric = (MetricDto) o;

        return getName() != null ? getName().equals(metric.getName()) : metric.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
