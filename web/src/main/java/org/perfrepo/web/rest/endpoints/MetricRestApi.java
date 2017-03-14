package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.web.adapter.MetricAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Service endpoint for test metrics.
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
      MetricDto metric = metricAdapter.getMetric(metricId);

      return Response.ok(metric).build();
   }

   @GET
   public Response getAll() {
      List<MetricDto> metrics = metricAdapter.getAllMetrics();

      return Response.ok().entity(metrics).build();
   }

   @PUT
   public Response update(MetricDto metricDto) {
      metricAdapter.updateMetric(metricDto);

      return Response.noContent().build();
   }
}