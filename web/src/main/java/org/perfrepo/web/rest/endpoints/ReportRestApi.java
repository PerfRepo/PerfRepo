package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.ReportAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Service endpoint for reports.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/reports")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReportRestApi {

   @Inject
   private ReportAdapter reportAdapter;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long reportId) {
      ReportDto report = reportAdapter.getReport(reportId);

      return Response.ok(report).build();
   }

   @POST
   public Response create(ReportDto reportDto) {
      ReportDto createdReport = reportAdapter.createReport(reportDto);

      URI uri = URI.create("/alerts/" + createdReport.getId());

      return Response.created(uri).build();
   }

   @PUT
   public Response update(ReportDto reportDto) {
      reportAdapter.updateReport(reportDto);

      return Response.noContent().build();
   }

   @POST
   @Path("/search")
   public Response search(ReportSearchCriteria searchParams) {
      SearchResult<ReportDto> result = reportAdapter.searchReports(searchParams);

      return Response
              .status(Response.Status.OK)
              .header("X-Pagination-Total-Count", result.getTotalCount())
              .header("X-Pagination-Current-Page", result.getCurrentPage())
              .header("X-Pagination-Page-Count", result.getPageCount())
              .header("X-Pagination-Per-Page", result.getPerPage())
              .entity(result.getData()).build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long reportId) {
      reportAdapter.removeReport(reportId);

      return Response.noContent().build();
   }

   @GET
   public Response getAll() {
      List<ReportDto> allReports = reportAdapter.getAllReports();

      return Response.ok().entity(allReports).build();
   }

   @GET
   @Path("/wizard/default-permissions")
   public Response getDefaultPermissions() {
      List<PermissionDto> permissions = reportAdapter.getDefaultReportPermissions();

      return Response.ok().entity(permissions).build();
   }

   @POST
   @Path("/wizard/validate/info-step")
   public Response validateReportInfoStep(ReportDto reportDto) {
      reportAdapter.validateWizardReportInfoStep(reportDto);
      return Response.ok().build();
   }

   @POST
   @Path("/wizard/validate/configuration-step")
   public Response validateReportConfigurationStep(ReportDto reportDto) {
      reportAdapter.validateWizardReportConfigurationStep(reportDto);
      return Response.ok().build();
   }

   @POST
   @Path("/wizard/validate/permission-step")
   public Response validateReportPermissionStep(ReportDto reportDto) {
      reportAdapter.validateWizardReportPermissionStep(reportDto);
      return Response.ok().build();
   }
}