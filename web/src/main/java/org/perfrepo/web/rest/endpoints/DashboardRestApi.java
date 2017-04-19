package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.dashboard.DashboardContent;
import org.perfrepo.web.adapter.DashboardAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Web service endpoint for dashboard page.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/dashboard")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DashboardRestApi {

    @Inject
    private DashboardAdapter dashboardAdapter;

    @GET
    @Path("/content")
    public Response getContent() {
        DashboardContent content = dashboardAdapter.getDashboardContent();

        return Response.ok().entity(content).build();
    }
}