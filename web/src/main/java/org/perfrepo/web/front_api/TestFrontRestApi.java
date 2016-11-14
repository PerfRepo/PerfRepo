package org.perfrepo.web.front_api;

import org.perfrepo.model.Test;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.web.dto.TestDto;
import org.perfrepo.web.front_api.validation.TestUidUnique;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.service.model_mapper.TestModelMapper;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;

@Path("/tests")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestFrontRestApi {

   @Inject
   private TestService testService;

   @Inject
   private TestModelMapper testModelMapper;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long testId) {
      Test test = testService.getFullTest(testId);

      if(test == null) {
         throw new NotFoundException("Test not found");
      }

      TestDto testDto = testModelMapper.convertToDto(test);
      return Response.ok(testDto).build();
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

      SearchResultWrapper<Test> tests = testService.searchTest(searchTO);
      Collection<TestDto> result =  testModelMapper.convertToDtoList(tests.getResult());
      //TODO add X-Pagination-Count, X-Pagination-Page, X-Pagination-Limit
      return Response.status(Response.Status.OK).entity(result).build();
   }

   @GET
   @Path("/uid/{uid}")
   public Response getByUid(@PathParam("uid") String testUid) {
      Test test = testService.getTestByUID(testUid);

      if(test == null) {
         throw new NotFoundException("Test not found");
      }
      // TODO this doesn't work, metrics are lazy initialized, solution? new service method with "full" test
      // TODO or change session context?
      TestDto testDto = testModelMapper.convertToDto(test);
      return Response.ok(testDto).build();
   }

   @POST
   public Response create(@Valid @TestUidUnique TestDto testDto) throws ServiceException {
      Test test = testModelMapper.convertToEntity(testDto);

      Test createdTest = testService.createTest(test);

      URI uri = URI.create("/tests/" + createdTest.getId());
      return Response.created(uri).build();
   }

   @PUT
   @Path("/{id}")
   public Response update(@Valid TestDto testDto) throws ServiceException {
      Test test = testModelMapper.convertToEntity(testDto);
      Test originalTest = testService.getTest(test.getId());

      if(originalTest == null){
         throw new NotFoundException("Test not found");
      }

      testService.updateTest(test);
      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long testId) throws ServiceException {
      Test test = testService.getTest(testId);

      if(test == null){
         throw new NotFoundException("Test not found");
      }

      testService.removeTest(test);
      return Response.noContent().build();
   }



}