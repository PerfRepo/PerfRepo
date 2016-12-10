package org.perfrepo.dto.util.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents validation
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationError {

    private List<FieldError> fieldErrors = new ArrayList<>();

    /**
     *
     * @param fieldError
     */
    public void addFieldError(FieldError fieldError) {
        fieldErrors.add(fieldError);
    }

    /**
     *
     *
     * @param field
     * @param message
     */
    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldError(field, message));
    }

    /**
     *
     *
     * @return
     */
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}
