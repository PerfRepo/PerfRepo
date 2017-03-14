package org.perfrepo.dto.report.metric_history;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ChartDto {

    private String name;

    private String description;

    private List<SeriesDto> series;

    private List<BaselineDto> baselines;

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

    public List<SeriesDto> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesDto> series) {
        this.series = series;
    }

    public List<BaselineDto> getBaselines() {
        return baselines;
    }

    public void setBaselines(List<BaselineDto> baselines) {
        this.baselines = baselines;
    }
}
