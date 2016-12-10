package org.perfrepo.web.rest.exception_mapper;

import org.perfrepo.web.adapter.exceptions.AdapterException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class AdapterExceptionMapper implements ExceptionMapper<AdapterException> {

    @Override
    public Response toResponse(AdapterException exception) {
        Map<String, Object> message = new HashMap<>();
        message.put("message", exception.getMessage());
        message.put("source", "ADAPTER EXCEPTION");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }
}