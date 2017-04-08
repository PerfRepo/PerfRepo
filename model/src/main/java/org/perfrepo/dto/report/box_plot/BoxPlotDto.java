package org.perfrepo.dto.report.box_plot;

import org.perfrepo.enums.report.BoxPlotSortType;
import org.perfrepo.enums.report.BoxPlotLabelType;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BoxPlotDto {

    private String name;

    private String description;

    private BoxPlotSeriesDto series;

    private BoxPlotLabelType labelType;

    private BoxPlotSortType sortType;

    private String labelParameter;

    private String sortParameter;

    private String sortVersionParameter;

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

    public BoxPlotSeriesDto getSeries() {
        return series;
    }

    public void setSeries(BoxPlotSeriesDto series) {
        this.series = series;
    }

    public BoxPlotLabelType getLabelType() {
        return labelType;
    }

    public void setLabelType(BoxPlotLabelType labelType) {
        this.labelType = labelType;
    }

    public BoxPlotSortType getSortType() {
        return sortType;
    }

    public void setSortType(BoxPlotSortType sortType) {
        this.sortType = sortType;
    }

    public String getLabelParameter() {
        return labelParameter;
    }

    public void setLabelParameter(String labelParameter) {
        this.labelParameter = labelParameter;
    }

    public String getSortParameter() {
        return sortParameter;
    }

    public void setSortParameter(String sortParameter) {
        this.sortParameter = sortParameter;
    }

    public String getSortVersionParameter() {
        return sortVersionParameter;
    }

    public void setSortVersionParameter(String sortVersionParameter) {
        this.sortVersionParameter = sortVersionParameter;
    }
}