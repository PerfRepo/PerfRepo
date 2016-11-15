package org.perfrepo.web.front_api;

import org.perfrepo.web.dto.UserGroupDto;
import org.perfrepo.web.front_api.storage.Storage;

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
    private Storage storage;

    @GET
    @Path("/groups")
    public Response getAllGroups() {
        List<UserGroupDto> userGroups = storage.userGroup().getAll();
        return Response.ok(userGroups).build();
    }

}