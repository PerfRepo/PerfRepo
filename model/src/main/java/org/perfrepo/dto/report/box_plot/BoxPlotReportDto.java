package org.perfrepo.dto.report.box_plot;

import org.perfrepo.dto.report.ReportDto;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BoxPlotReportDto extends ReportDto {

    private List<BoxPlotDto> boxPlots;

    public List<BoxPlotDto> getBoxPlots() {
        return boxPlots;
    }

    public void setBoxPlots(List<BoxPlotDto> boxPlots) {
        this.boxPlots = boxPlots;
    }
}
