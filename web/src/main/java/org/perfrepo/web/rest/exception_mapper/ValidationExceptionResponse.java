package org.perfrepo.web.rest.exception_mapper;

import org.perfrepo.dto.util.validation.FieldError;
import org.perfrepo.web.adapter.exceptions.ValidationException;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationExceptionResponse {

    private String message;
    private String source;
    private List<FieldError> fieldErrors;
    private List<FieldError> formErrors;

    ValidationExceptionResponse(ValidationException exception, String source) {
        this.message = exception.getMessage();
        this.source = source;
        this.fieldErrors = exception.getValidationErrors().getFieldErrors();
        this.formErrors = exception.getValidationErrors().getFormErrors();
    }

    ValidationExceptionResponse(ValidationException exception) {
        this(exception, "VALIDATION EXCEPTION");
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