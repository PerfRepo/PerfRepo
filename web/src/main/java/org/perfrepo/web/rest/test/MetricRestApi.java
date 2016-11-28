package org.perfrepo.web.rest.test;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.web.adapter.test.MetricAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/metrics")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MetricRestApi {

   @Inject
   private MetricAdapter metricAdapter;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long metricId) {
      MetricDto metric = metricAdapter.getMetricById(metricId);

      return Response.ok(metric).build();
   }

   @GET
   public Response getAll() {
      List<MetricDto> metrics = metricAdapter.getAllMetrics();

      return Response.ok().entity(metrics).build();
   }

   @POST
   public Response create(MetricDto metricDto) {
      MetricDto createdMetric = metricAdapter.createMetric(metricDto);

      URI uri = URI.create("/metrics/" + createdMetric.getId());
      return Response.created(uri).build();
   }

   @PUT
   @Path("/{id}")
   public Response update(MetricDto metricDto) {
      MetricDto updatedMetric = metricAdapter.updateMetric(metricDto);

      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long metricId) {
      metricAdapter.deleteMetric(metricId);

      return Response.noContent().build();
   }
}