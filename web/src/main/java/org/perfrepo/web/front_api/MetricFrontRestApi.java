package org.perfrepo.web.front_api;

import org.perfrepo.web.dto.MetricDto;
import org.perfrepo.web.front_api.storage.Storage;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/metrics")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MetricFrontRestApi {

   @Inject
   private Storage storage;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long metricId) {
      MetricDto metric = storage.metric().getById(metricId);

      if(metric == null) {
         throw new NotFoundException("Metric not found");
      }

      return Response.ok(metric).build();
   }

   @GET
   public Response getAll() {
      List<MetricDto> metrics = storage.metric().getAll();

      return Response.status(Response.Status.OK).entity(metrics).build();
   }

   @POST
   public Response create(MetricDto metricDto) {
      MetricDto createdMetric = storage.metric().create(metricDto);

      URI uri = URI.create("/metrics/" + createdMetric.getId());
      return Response.created(uri).build();
   }

   @PUT
   @Path("/{id}")
   public Response update(MetricDto metricDto) {
      MetricDto updatedMetric = storage.metric().update(metricDto);

      if(updatedMetric == null) {
         throw new NotFoundException("Metric not found");
      }

      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long testId) {
      boolean deleted = storage.test().delete(testId);

      if(!deleted) {
         throw new NotFoundException("Metric not found");
      }

      return Response.noContent().build();
   }

}