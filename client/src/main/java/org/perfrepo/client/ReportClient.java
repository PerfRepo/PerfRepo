package org.perfrepo.client;

import java.util.List;

import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;

/**
 *
 * @author xkrajcik
 */
public class ReportClient {

    private PerfRepoClient parentClient;

    public ReportClient(PerfRepoClient parentClient) {
        this.parentClient = parentClient;
    }

    public ReportDto getById(Long reportId) {
        return null;
    }

    public void create(ReportDto reportDto) {

    }

    public void update(ReportDto reportDto) {

    }

    public void detete(Long reportId) {

    }

    public List<ReportDto> search(ReportSearchCriteria searchParams) {
        return null;
    }

    public List<ReportDto> getAll() {
        return null;
    }

    public boolean validateReportInfoStep(ReportDto reportDto) {
        return false;
    }

    public boolean validateReportConfigurationStep(ReportDto reportDto) {
        return false;
    }

    public boolean validateReportPermissionStep(ReportDto reportDto) {
        return false;
    }

}
