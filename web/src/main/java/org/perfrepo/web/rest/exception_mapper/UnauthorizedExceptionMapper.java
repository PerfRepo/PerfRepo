package org.perfrepo.web.rest.exception_mapper;

import org.apache.commons.collections.map.HashedMap;
import org.perfrepo.web.adapter.exceptions.UnauthorizedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException exception) {
        Map<String, Object> message = new HashedMap();
        message.put("message", exception.getMessage());
        message.put("source", "UNAUTHORIZED EXCEPTION");
        return Response.status(Response.Status.FORBIDDEN).entity(message).build();
    }
}