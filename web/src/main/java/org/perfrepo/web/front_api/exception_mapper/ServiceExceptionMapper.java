package org.perfrepo.web.front_api.exception_mapper;

import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.util.MessageUtils;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

    @Override
    public Response toResponse(ServiceException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(MessageUtils.getMessage(exception)).build();
    }
}