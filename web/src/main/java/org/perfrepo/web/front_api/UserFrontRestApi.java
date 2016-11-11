package org.perfrepo.web.front_api;

import org.perfrepo.web.service.UserService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserFrontRestApi {

    @Inject
    private UserService userService;

    @GET
    @Path("/user-group-names")
    public Response userGroupNames() {
        List<String> userGroupNames =  userService.getLoggedUserGroupNames();

        return Response.ok(userGroupNames).build();
    }
}