package org.perfrepo.web.rest.exception_mapper;

import org.apache.http.HttpStatus;
import org.perfrepo.web.adapter.exceptions.ValidationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps {@link ValidationException} exception to HTTP response. Sets 422 status code.
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        ValidationExceptionResponse responseEntity = new ValidationExceptionResponse(exception);

        return Response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).entity(responseEntity).build();
    }
}