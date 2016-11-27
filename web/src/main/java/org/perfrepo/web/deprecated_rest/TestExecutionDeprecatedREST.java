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
package org.perfrepo.web.deprecated_rest;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionAttachment;
import org.perfrepo.model.Value;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.deprecated_rest.logging.Logged;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST interface for test execution related operations.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Path("/testExecution")
@RequestScoped
public class TestExecutionDeprecatedREST {

   private static Method GET_TEST_EXECUTION_METHOD;
   private static Method GET_ATTACHMENT_METHOD;

   static {
      try {
         GET_TEST_EXECUTION_METHOD = TestExecutionDeprecatedREST.class.getMethod("get", Long.class);
         GET_ATTACHMENT_METHOD = TestExecutionDeprecatedREST.class.getMethod("getAttachment", Long.class);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }
   }

   @Inject
   private TestService testService;

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/{testExecutionId}")
   @Logged
   public Response get(@PathParam("testExecutionId") Long testExecutionId) {
      return Response.ok(testService.getFullTestExecution(testExecutionId)).build();
   }

   @POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response create(TestExecution testExecution, @Context UriInfo uriInfo) throws ServiceException {
      Test test = null;
      if (testExecution.getTest().getId() != null) {
         test = testService.getFullTest(testExecution.getTest().getId());
      } else {
         test = testService.getTestByUID(testExecution.getTest().getUid());
      }
      testExecution.setTest(test);

      if (testExecution.getStarted() == null) {
         testExecution.setStarted(new Date());
      }

      Long id = testService.createTestExecution(testExecution).getId();
      return Response.created(uriInfo.getBaseUriBuilder().path(TestExecutionDeprecatedREST.class).path(GET_TEST_EXECUTION_METHOD).build(id)).entity(id).build();
   }

   @POST
   @Path("/update/{testExecutionId}")
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response update(TestExecution testExecution, @Context UriInfo uriInfo) throws Exception {
      Test test = null;
      if (testExecution.getTest().getId() != null) {
         test = testService.getFullTest(testExecution.getTest().getId());
      } else {
         test = testService.getTestByUID(testExecution.getTest().getUid());
      }

      testExecution.setTest(test);
      testService.updateTestExecution(testExecution);

      return Response.created(uriInfo.getBaseUriBuilder().path(TestExecutionDeprecatedREST.class).path(GET_TEST_EXECUTION_METHOD).build(testExecution.getId())).entity(testExecution.getId()).build();
   }

   @POST
   @Path("/search")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.APPLICATION_XML)
   @Wrapped(element = "testExecutions")
   @Logged
   public Response search(TestExecutionSearchTO criteria) {
      SearchResultWrapper<TestExecution> searchResultWrapper = testService.searchTestExecutions(criteria);
      List<TestExecution> result = testService.getFullTestExecutions(searchResultWrapper.getResult().stream().map(TestExecution::getId).collect(Collectors.toList()));
      GenericEntity<List<TestExecution>> entity = new GenericEntity<List<TestExecution>>(result) { };
      return Response.ok(entity).build();
   }

   @POST()
   @Path("/addValue")
   @Consumes(MediaType.TEXT_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response addValue(TestExecution te, @Context UriInfo uriInfo) throws Exception {
      Collection<Value> values = te.getValues();
      Value value = values.iterator().next();
      value.setTestExecution(te);
      Value result = testService.addValue(value);
      return Response.created(uriInfo.getBaseUriBuilder().path(TestExecutionDeprecatedREST.class).path(GET_TEST_EXECUTION_METHOD).build(result.getId())).entity(result.getId()).build();
   }

   @DELETE
   @Produces(MediaType.TEXT_XML)
   @Path("/{testExecutionId}")
   @Logged
   public Response delete(@PathParam("testExecutionId") Long testExecutionId) throws Exception {
      TestExecution idHolder = new TestExecution();
      idHolder.setId(testExecutionId);
      testService.removeTestExecution(idHolder);
      return Response.noContent().build();
   }

   @POST()
   @Path("/{testExecutionId}/addAttachment")
   @Produces(MediaType.TEXT_PLAIN)
   @Logged
   public Response addAttachment(@PathParam("testExecutionId") Long testExecutionId, @HeaderParam("Content-type") String mimeType,
                                 @HeaderParam("filename") String fileName, byte[] requestBody, @Context UriInfo uriInfo) throws Exception {
      TestExecutionAttachment attachment = new TestExecutionAttachment();
      attachment.setFilename(fileName);
      attachment.setMimetype(mimeType);
      attachment.setContent(requestBody);
      TestExecution testExec = new TestExecution();
      testExec.setId(testExecutionId);
      attachment.setTestExecution(testExec);
      Long id = testService.addAttachment(attachment);
      return Response.created(uriInfo.getBaseUriBuilder().path(TestExecutionDeprecatedREST.class).path(GET_ATTACHMENT_METHOD).build(id)).entity(id).build();
   }

   @GET
   @Path("/attachment/{attachmentId}")
   @Logged
   public Response getAttachment(@PathParam("attachmentId") Long attachmentId) {
      TestExecutionAttachment attachment = testService.getAttachment(attachmentId);
      if (attachment == null) {
         return Response.status(Status.NOT_FOUND).build();
      }
      ResponseBuilder response = Response.ok(attachment.getContent());
      response.header("Content-type", attachment.getMimetype());
      response.header("Content-Disposition", "attachment; filename=" + attachment.getFilename());
      return response.build();
   }
}