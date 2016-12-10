package org.perfrepo.web.rest.exception_mapper;

import org.apache.http.HttpStatus;
import org.perfrepo.dto.util.validation.ValidationError;
import org.perfrepo.web.adapter.exceptions.ValidationException;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {

        ValidationExceptionResponse responseEntity = new ValidationExceptionResponse(exception.getMessage(), exception.getValidationErrors());

        return Response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).entity(responseEntity).build();
    }

    private class ValidationExceptionResponse {

        private String message;

        private ValidationError validation;

        public ValidationExceptionResponse(String message, ValidationError validationErrors) {
            this.message = message;
            this.validation = validationErrors;
        }

        public String getMessage() {
            return message;
        }

        public ValidationError getValidation() {
            return validation;
        }
    }
}