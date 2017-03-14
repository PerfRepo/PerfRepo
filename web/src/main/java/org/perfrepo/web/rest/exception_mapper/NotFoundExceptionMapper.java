package org.perfrepo.web.rest.exception_mapper;

import org.perfrepo.web.adapter.exceptions.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps {@link NotFoundException} exception to HTTP response. Sets 404 status code.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        Map<String, Object> message = new HashMap<>();
        message.put("message", exception.getMessage());
        message.put("source", "NOT_FOUND EXCEPTION");
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }
}