package org.perfrepo.web.rest.endpoints;

import org.perfrepo.web.service.ApplicationConfiguration;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Web service endpoint for general purposes.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeneralRestApi {

    @Inject
    private ApplicationConfiguration applicationConfiguration;

    @GET
    @Path("/info")
    public Response getInfo() {
        Map<String, Object> content = new HashMap<>();

        content.put("version", applicationConfiguration.getPerfRepoVersion());

        return Response.ok().entity(content).build();
    }
}