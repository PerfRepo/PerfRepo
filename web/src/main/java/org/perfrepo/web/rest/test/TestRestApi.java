package org.perfrepo.web.rest.test;

import org.perfrepo.dto.SearchResult;
import org.perfrepo.dto.test.TestSearchParams;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.web.adapter.test.TestAdapter;

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
@Path("/tests")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestRestApi {

   @Inject
   private TestAdapter testAdapter;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long testId) {
      TestDto test = testAdapter.getTestById(testId);

      return Response.ok(test).build();
   }

   @POST
   @Path("/search")
   public Response search(TestSearchParams searchParams) {
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
      TestDto test = testAdapter.getTestByUid(testUid);

      return Response.ok(test).build();
   }

   @POST
   public Response create(TestDto testDto) {
      TestDto createdTest = testAdapter.createTest(testDto);

      URI uri = URI.create("/tests/" + createdTest.getId());
      return Response.created(uri).build();
   }

   @PUT
   @Path("/{id}")
   public Response update(TestDto testDto) {
      TestDto updatedTest = testAdapter.updateTest(testDto);

      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long testId) {
      testAdapter.deleteTest(testId);

      return Response.noContent().build();
   }

   @GET
   public Response getAll() {
      List<TestDto> allTests = testAdapter.getAllTests();

      return Response.ok().entity(allTests).build();
   }
}