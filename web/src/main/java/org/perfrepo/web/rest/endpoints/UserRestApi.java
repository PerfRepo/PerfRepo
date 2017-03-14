package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.user.UserDto;
import org.perfrepo.web.adapter.UserAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Service endpoint for users.
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
    public Response getAllUsers() {
        List<UserDto> allUsers = userAdapter.getAllUsers();

        return Response.ok().entity(allUsers).build();
    }
}