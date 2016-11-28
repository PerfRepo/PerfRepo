package org.perfrepo.web.rest.exception_mapper;

import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.util.MessageUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class AdapterExceptionMapper implements ExceptionMapper<AdapterException> {

    @Override
    public Response toResponse(AdapterException exception) {
        return Response.status(Response.Status.OK).entity("ADAPTER EXCEPTION: " + exception.getMessage() + "  " + exception.getClass().getSimpleName()).build();
    }
}