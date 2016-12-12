package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.web.adapter.AlertAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/alerts")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AlertRestApi {

    @Inject
    private AlertAdapter alertAdapter;

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long alertId) {
        AlertDto alert = alertAdapter.getAlert(alertId);

        return Response.ok(alert).build();
    }

    @POST
    public Response create(AlertDto alertDto) {
        AlertDto createdAlert = alertAdapter.createAlert(alertDto);

        URI uri = URI.create("/alerts/" + createdAlert.getId());

        return Response.created(uri).build();
    }

    @PUT
    public Response update(AlertDto alertDto) {
        alertAdapter.updateAlert(alertDto);

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long alertId) {
        alertAdapter.removeAlert(alertId);

        return Response.noContent().build();
    }
}