package org.perfrepo.dto.util.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents validation
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationErrorDto {

    private List<FieldErrorDto> fieldErrors = new ArrayList<>();

    /**
     *
     * @param fieldError
     */
    public void addFieldError(FieldErrorDto fieldError) {
        fieldErrors.add(fieldError);
    }

    /**
     *
     *
     * @param field
     * @param message
     */
    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldErrorDto(field, message));
    }

    /**
     *
     *
     * @return
     */
    public List<FieldErrorDto> getFieldErrors() {
        return fieldErrors;
    }
}
