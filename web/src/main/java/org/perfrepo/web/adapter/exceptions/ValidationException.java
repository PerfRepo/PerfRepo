package org.perfrepo.web.adapter.exceptions;

import org.perfrepo.dto.util.validation.ValidationError;

/**
 * This unchecked exception is thrown when service adapter performed validation
 * and it was not successful. The exception contains {@link ValidationError} object
 * which contains the list of particular validation
 * errors ({@link org.perfrepo.dto.util.validation.FieldError}).
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationException extends AdapterException {

    private ValidationError validationErrors;

    /**
     * Constructs a {@link ValidationException} with the specified detail message
     * and validation errors.
     *
     * @param message Detailed message.
     * @param validationErrors The list of fields validation errors.
     */
    public ValidationException(String message, ValidationError validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    /**
     * Returns {@link ValidationError} object
     * which contains validation errors.
     *
     * @return {@link ValidationError} object which contains the list of particular validation
     * errors ({@link FieldError}).
     */
    public ValidationError getValidationErrors() {
        return validationErrors;
    }
}
