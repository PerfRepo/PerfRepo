package org.perfrepo.web.front_api;

import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.web.dto.TestDto;
import org.perfrepo.web.front_api.storage.Storage;
import org.perfrepo.web.front_api.storage.TestStorage;
import org.perfrepo.web.front_api.validation.TestUidUnique;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/tests")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestFrontRestApi {

   @Inject
   private Storage storage;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long testId) {
      TestDto test = storage.test().getById(testId);

      if(test == null) {
         throw new NotFoundException("Test not found");
      }

      return Response.ok(test).build();
   }

   @GET
   public Response search(@QueryParam("name") String name,
                          @QueryParam("uid") String uid,
                          @QueryParam("orderBy") String orderBy,
                          @QueryParam("group") String group,
                          @QueryParam("groupFilter") String groupFilter,
                          @QueryParam("limit") Integer limit,
                          @QueryParam("offset") Integer offset) {

      TestSearchTO searchTO = new TestSearchTO();
      searchTO.setName(name);
      searchTO.setUid(uid);
      searchTO.setGroupId(group);
      searchTO.setOrderBy(OrderBy.valueOf(orderBy));  // TODO Illegal argument exception
      searchTO.setGroupFilter(GroupFilter.valueOf(groupFilter)); // TODO Illegal argument exception
      searchTO.setLimitFrom(offset);
      searchTO.setLimitHowMany(limit);

      TestStorage.TestSearchResult result = storage.test().search(searchTO);

      return Response
              .status(Response.Status.OK)
              .header("X-Pagination-Total-Count", result.getTotalCount())
              .header("X-Pagination-Current-Page", result.getCurrentPage())
              .header("X-Pagination-Page-Count", result.getPageCount())
              .header("X-Pagination-Per-Page", result.getPerPage())
              .entity(result.getTests()).build();
   }

   @GET
   @Path("/uid/{uid}")
   public Response getByUid(@PathParam("uid") String testUid) {
      TestDto test = storage.test().getByUid(testUid);

      if(test == null) {
         throw new NotFoundException("Test not found");
      }

      return Response.ok(test).build();
   }

   @POST
   public Response create(@Valid @TestUidUnique TestDto testDto) {
      TestDto createdTest = storage.test().create(testDto);

      URI uri = URI.create("/tests/" + createdTest.getId());
      return Response.created(uri).build();
   }

   @PUT
   @Path("/{id}")
   public Response update(@Valid TestDto testDto) {
      TestDto updatedTest = storage.test().update(testDto);

      if(updatedTest == null) {
         throw new NotFoundException("Test not found");
      }

      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long testId) throws ServiceException {
      boolean deleted = storage.test().delete(testId);

      if(!deleted) {
         throw new NotFoundException("Test not found");
      }

      return Response.noContent().build();
   }

}