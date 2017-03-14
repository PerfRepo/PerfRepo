package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.web.adapter.GroupAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Service endpoint for test alerts.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/groups")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupRestApi {

    @Inject
    private GroupAdapter groupAdapter;

    @GET
    public Response getAllGroups() {
        List<GroupDto> groups = groupAdapter.getAllGroups();

        return Response.ok(groups).build();
    }
}