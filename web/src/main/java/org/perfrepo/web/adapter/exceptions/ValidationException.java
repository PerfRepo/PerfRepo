package org.perfrepo.web.adapter.exceptions;

import org.perfrepo.dto.util.validation.ValidationErrors;

/**
 * This unchecked exception is thrown when if the validation of data was not successful.
 * The exception contains {@link ValidationErrors} object
 * which contains the list of particular validation
 * errors ({@link org.perfrepo.dto.util.validation.FieldError}).
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationException extends AdapterException {

    private ValidationErrors validationErrors;

    /**
     * Constructs a {@link ValidationException} with the specified detail message
     * and validation errors.
     *
     * @param message Detailed message.
     * @param validationErrors The list of fields validation errors.
     */
    public ValidationException(String message, ValidationErrors validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    /**
     * Returns {@link ValidationErrors} object
     * which contains validation errors.
     *
     * @return {@link ValidationErrors} object which contains the list of particular validation
     * errors ({@link org.perfrepo.dto.util.validation.FieldError}).
     */
    public ValidationErrors getValidationErrors() {
        return validationErrors;
    }
}
