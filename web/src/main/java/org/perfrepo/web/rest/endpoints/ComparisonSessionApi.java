package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.web.adapter.ComparisonSessionAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * Service endpoint for test execution comparison session.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/comparison-session")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComparisonSessionApi {

    @Inject
    private ComparisonSessionAdapter comparisonSessionAdapter;

    @GET
    public Response getTestExecutions() {
        List<TestExecutionDto> testExecutions = comparisonSessionAdapter.getTestExecutions();

        return Response.ok(testExecutions).build();
    }

    @POST
    @Path("/addition")
    public Response addToComparison(Set<Long> testExecutionIds) {
        List<TestExecutionDto> testExecutions = comparisonSessionAdapter.addToComparison(testExecutionIds);

        return Response.ok(testExecutions).build();
    }

    @POST
    @Path("/removal")
    public Response removeFromComparison(Set<Long> testExecutionIds) {
        List<TestExecutionDto> testExecutions = comparisonSessionAdapter.removeFromComparison(testExecutionIds);

        return Response.ok(testExecutions).build();
    }
}