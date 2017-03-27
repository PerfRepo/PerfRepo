package org.perfrepo.web.adapter;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.util.SearchResult;

import java.util.List;

/**
 * Service adapter for reports. Adapter provides operations for
 * {@link ReportDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface ReportAdapter {
    /**
     * Return {@link ReportDto} object by its id.
     *
     * @param id The report identifier.
     *
     * @return Found {@link ReportDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    ReportDto getReport(Long id);

    /**
     * Create new {@link ReportDto} object.
     *
     * @param report Parameters of the report that will be created.
     *
     * @return The created {@link ReportDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    ReportDto createReport(ReportDto report);

    /**
     * Update the {@link ReportDto} object.
     *
     * @param report Parameters of the report that will be updated.
     *
     * @return Updated {@link ReportDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    ReportDto updateReport(ReportDto report);

    /**
     * Remove the {@link ReportDto} object.
     *
     * @param id The report identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeReport(Long id);

    /**
     * Return all reports.
     *
     * @return List of all reports.
     */
    List<ReportDto> getAllReports();

    /**
     * Return all {@link ReportDto} reports that satisfy search conditions.
     *
     * @param searchParams The report search criteria params.
     *
     * @return List of {@link ReportDto} tests.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    SearchResult<ReportDto> searchReports(ReportSearchCriteria searchParams);

    /**
     * Return saved report search params.
     *
     * @return Search criteria params.
     */
    ReportSearchCriteria getSearchCriteria();

    /**
     * Return default permissions for new report.
     *
     * @return List of access permissions.
     */
    List<PermissionDto> getDefaultReportPermissions();

    /**
     * Validate report wizard step - report information (name, description...).
     *
     * @param report Parameters of the report that will be validated.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void validateWizardReportInfoStep(ReportDto report);

    /**
     * Validate table comparison report wizard step - whole configuration (groups, comparison tables, compariosn items).
     *
     * @param report Parameters of the report that will be validated.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void validateWizardReportConfigurationStep(ReportDto report);

    /**
     * Validate report wizard step - report permissions.
     *
     * @param report Parameters of the report that will be validated.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void validateWizardReportPermissionStep(ReportDto report);
}