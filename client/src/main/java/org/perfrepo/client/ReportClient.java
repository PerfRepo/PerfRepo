/*
 * Copyright 2017 xkrajcik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.client;

import java.net.URI;
import java.util.Collection;
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

    public ReportDto getReportById(Long reportId) {

    }

    public Collection<ReportDto> search(ReportSearchCriteria searchParams) {

    }

    public URI createReport(ReportDto reportDto) {

    }

    public void updateReport(ReportDto reportDto) {

    }

    public void deteteReport(Long reportId) {

    }

    public Collection<ReportDto> getAllReports() {

    }

    public boolean validateReportInfoStep(ReportDto reportDto) {

    }

    public boolean validateReportConfigurationStep(ReportDto reportDto) {

    }

    public boolean validateReportPermissionStep(ReportDto reportDto) {

    }

}
