package org.perfrepo.web.adapter.exceptions;

import org.perfrepo.dto.util.validation.FieldErrorDto;
import org.perfrepo.dto.util.validation.ValidationErrorDto;

/**
 * This unchecked exception is thrown when service adapter performed validation
 * and it was not successful. The exception contains {@link ValidationErrorDto} object
 * which contains the list of particular validation errors ({@link FieldErrorDto}).
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationException extends AdapterException {

    private ValidationErrorDto validationErrors;

    /**
     * Constructs a {@link ValidationException} with the specified detail message
     * and validation errors.
     *
     * @param message Detailed message.
     * @param validationErrors The list of fields validation errors.
     */
    public ValidationException(String message, ValidationErrorDto validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    /**
     * Returns {@link ValidationErrorDto} object
     * which contains validation errors.
     *
     * @return {@link ValidationErrorDto} object which contains the list of particular validation
     * errors ({@link FieldErrorDto}).
     */
    public ValidationErrorDto getValidationErrors() {
        return validationErrors;
    }
}
