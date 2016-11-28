package org.perfrepo.web.rest;

import org.perfrepo.dto.user.GroupDto;
import org.perfrepo.web.adapter.user.UserAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/users")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestApi {

    @Inject
    private UserAdapter userAdapter;

    @GET
    @Path("/groups")
    public Response getAllGroups() {
        List<GroupDto> groups = userAdapter.getAllGroups();
        return Response.ok(groups).build();
    }
}