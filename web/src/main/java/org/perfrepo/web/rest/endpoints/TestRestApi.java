package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.test.TestSearchCriteria;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.web.adapter.AlertAdapter;
import org.perfrepo.web.adapter.MetricAdapter;
import org.perfrepo.web.adapter.TestAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Service endpoint for test definitions.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/tests")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestRestApi {

   @Inject
   private TestAdapter testAdapter;

   @Inject
   private MetricAdapter metricAdapter;

   @Inject
   private AlertAdapter alertAdapter;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long testId) {
      TestDto test = testAdapter.getTest(testId);

      return Response.ok(test).build();
   }

   @POST
   @Path("/search")
   public Response search(TestSearchCriteria searchParams) {
      SearchResult<TestDto> result = testAdapter.searchTests(searchParams);

      return Response
              .status(Response.Status.OK)
              .header("X-Pagination-Total-Count", result.getTotalCount())
              .header("X-Pagination-Current-Page", result.getCurrentPage())
              .header("X-Pagination-Page-Count", result.getPageCount())
              .header("X-Pagination-Per-Page", result.getPerPage())
              .entity(result.getData()).build();
   }

   @GET
   @Path("/uid/{uid}")
   public Response getByUid(@PathParam("uid") String testUid) {
      TestDto test = testAdapter.getTest(testUid);

      return Response.ok(test).build();
   }

   @POST
   public Response create(TestDto testDto) {
      TestDto createdTest = testAdapter.createTest(testDto);
      URI uri = URI.create("/tests/" + createdTest.getId());

      return Response.created(uri).build();
   }

   @PUT
   public Response update(TestDto testDto) {
      testAdapter.updateTest(testDto);

      return Response.noContent().build();
   }

   @POST
   @Path("/{id}/metric-addition")
   public Response addMetric(MetricDto metric, @PathParam("id") Long testId) {
      metricAdapter.addMetric(metric, testId);

      return Response.noContent().build();
   }

   @POST
   @Path("/{id}/metric-removal/{metricId}")
   public Response removeMetric(@PathParam("metricId") Long metricId, @PathParam("id") Long testId) {
      metricAdapter.removeMetric(metricId, testId);

      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long testId) {
      testAdapter.removeTest(testId);

      return Response.noContent().build();
   }

   /**
    * Return true if logged user is alert subscriber of the test.
    *
    * @param testId The test identifier.
    *
    * @return Return true if logged user is alert subscriber.
    */
   @GET
   @Path("/{id}/subscriber")
   public Response isUserSubscriber(@PathParam("id") Long testId) {
      boolean isSubscriber = testAdapter.isSubscriber(testId);
      return Response.ok().entity(isSubscriber).build();
   }

   /**
    * Subscribe logged user to the test alerts.
    *
    * @param testId The test identifier.
    *
    * @return No content if the request is successful.
    */
   @POST
   @Path("/{id}/subscriber-addition")
   public Response addSubscriber(@PathParam("id") Long testId) {
      testAdapter.addSubscriber(testId);

      return Response.noContent().build();
   }

   /**
    * Unsubscribe logged user to the test alerts.
    *
    * @param testId The test identifier.
    *
    * @return No content if the request is successful.
    */
   @POST
   @Path("/{id}/subscriber-removal")
   public Response removeSubscriber(@PathParam("id") Long testId) {
      testAdapter.removeSubscriber(testId);

      return Response.noContent().build();
   }

   /**
    * Return all defined alerts of the specified test.
    *
    * @param testId The test identifier.
    * @return List of alerts.
    */
   @GET
   @Path("/{id}/alerts")
   public Response getAllAlertsForTest(@PathParam("id") Long testId) {
      List<AlertDto> alerts = alertAdapter.getAllAlertsForTest(testId);

      return Response.ok().entity(alerts).build();
   }

   @GET
   public Response getAll() {
      List<TestDto> allTests = testAdapter.getAllTests();

      return Response.ok().entity(allTests).build();
   }
}