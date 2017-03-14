package org.perfrepo.web.rest.exception_mapper;

import org.apache.http.HttpStatus;
import org.perfrepo.dto.util.validation.FieldError;
import org.perfrepo.web.adapter.exceptions.ValidationException;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

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

    private class ValidationExceptionResponse {

        private String message;
        private String source = "VALIDATION EXCEPTION";
        private List<FieldError> fieldErrors;
        private List<FieldError> formErrors;

        ValidationExceptionResponse(ValidationException exception) {
            this.message = exception.getMessage();
            this.fieldErrors = exception.getValidationErrors().getFieldErrors();
            this.formErrors = exception.getValidationErrors().getFormErrors();
        }

        public String getMessage() {
            return message;
        }

        public String getSource() {
            return source;
        }

        public List<FieldError> getFieldErrors() {
            return fieldErrors;
        }

        public List<FieldError> getFormErrors() {
            return formErrors;
        }
    }
}