/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.rest;

import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;
import org.perfrepo.model.user.User;
import org.perfrepo.web.rest.logging.Logged;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;

/**
 * REST interface for Report objects.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Path("/report")
@RequestScoped
public class ReportREST {

   private static Method GET_REPORT_METHOD;

   @Inject
   private ReportService reportService;

   @Inject
   private UserService userService;

   static {
      try {
         GET_REPORT_METHOD = ReportREST.class.getMethod("get", Long.class);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }
   }

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/id/{reportId}")
   @Logged
   public Response get(@PathParam("reportId") Long reportId) {
      return Response.ok(reportService.getFullReport(new Report(reportId))).build();
   }

   @POST
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response create(Report report, @Context UriInfo uriInfo) throws Exception {
      String username = report.getUsername();
      User user = userService.getFullUser(username);
      report.setUser(user);

      if (report.getProperties() != null) {
         for (ReportProperty property : report.getProperties().values()) {
            property.setReport(report);
         }
      }

      Long id = reportService.createReport(report).getId();
      return Response.created(uriInfo.getBaseUriBuilder().path(ReportREST.class).path(GET_REPORT_METHOD).build(id)).entity(id).build();
   }

   @POST
   @Path("/update/{reportId}")
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response update(Report report, @Context UriInfo uriInfo) throws Exception {
      String username = report.getUsername();
      User user = userService.getFullUser(username);
      report.setUser(user);

      if (report.getProperties() != null) {
         for (ReportProperty property : report.getProperties().values()) {
            property.setReport(report);
         }
      }

      reportService.updateReport(report);
      return Response.created(uriInfo.getBaseUriBuilder().path(ReportREST.class).path(GET_REPORT_METHOD).build(report.getId())).entity(report.getId()).build();
   }

   @DELETE
   @Path("/id/{reportId}")
   @Logged
   public Response delete(@PathParam("reportId") Long reportId) throws Exception {
      Report report = new Report(reportId);
      reportService.removeReport(report);
      return Response.noContent().build();
   }

   @POST
   @Path("/id/{reportId}/addPermission")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response addPermission(Permission permission, @Context UriInfo uriInfo) throws ServiceException {
      reportService.addPermission(permission);

      return Response.ok().build();
   }

   @POST
   @Path("/id/{reportId}/updatePermission")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response updatePermission(Permission permission, @Context UriInfo uriInfo) throws ServiceException {
      reportService.updatePermission(permission);

      return Response.ok().build();
   }

   @POST
   @Path("/id/{reportId}/deletePermission")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response deletePermission(Permission permission, @Context UriInfo uriInfo) throws ServiceException {
      reportService.deletePermission(permission);

      return Response.ok().build();
   }
}