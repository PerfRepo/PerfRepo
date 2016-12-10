package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;
import org.perfrepo.web.adapter.AuthenticationAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationRestApi {

    @Inject
    private AuthenticationAdapter authenticationAdapter;

    @POST
    @Path("/authentication")
    public Response authenticate(LoginCredentialParams credentials) {
        AuthenticationResult token = authenticationAdapter.login(credentials);

        return Response.ok().entity(token).build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        // TODO get the token from header Authentication: BEARER <<token>>
        String token = "token";
        authenticationAdapter.logout(token);

        return Response.noContent().build();
    }
}