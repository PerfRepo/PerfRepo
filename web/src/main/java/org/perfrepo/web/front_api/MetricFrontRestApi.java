package org.perfrepo.web.front_api;

import org.perfrepo.model.Metric;
import org.perfrepo.web.dto.MetricDto;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.model_mapper.MetricModelMapper;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/metrics")
@RequestScoped
public class MetricFrontRestApi {

   @Inject
   private TestService testService;

   @Inject
   private MetricModelMapper metricModelMapper;

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   public Response get(@PathParam("id") Long metricId) {
      Metric metric = testService.getFullMetric(metricId);
      if(metric == null) {
         throw new NotFoundException("Metric not found");
      }

      MetricDto metricDto = metricModelMapper.convertToDto(metric);
      return Response.ok(metricDto).build();
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getAll() {
      return Response.status(Response.Status.NOT_IMPLEMENTED).build();
   }


}