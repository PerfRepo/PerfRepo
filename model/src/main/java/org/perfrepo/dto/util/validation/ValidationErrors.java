package org.perfrepo.dto.util.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents validation
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValidationErrors {

    private List<FieldError> fieldErrors = new ArrayList<>();
    private List<FieldError> formErrors = new ArrayList<>();

    /**
     * Add validation error for specific form field.
     *
     * @param fieldError Field validation error.
     */
    public void addFieldError(FieldError fieldError) {
        fieldErrors.add(fieldError);
    }

    /**
     * Add validation error to form.
     *
     * @param fieldError Field validation error.
     */
    public void addFormError(FieldError fieldError) {
        formErrors.add(fieldError);
    }

    /**
     * Add validation error for specific form field.
     *
     * @param name Form field name.
     * @param message Error message.
     */
    public void addFieldError(String name, String message) {
        fieldErrors.add(new FieldError(name, message));
    }

    /**
     * Add validation error to form.
     *
     * @param name Error name.
     * @param message Error message.
     */
    public void addFormError(String name, String message) {
        formErrors.add(new FieldError(name, message));
    }

    /**
     * Return list of field validation errors.
     *
     * @return List of field validation errors.
     */
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Return list of form validation errors.
     *
     * @return List of form validation errors.
     */
    public List<FieldError> getFormErrors() {
        return formErrors;
    }

    /**
     * Return true if it contains at least one validation error (form error or form field error).
     *
     * @return True if it contains validation errors.
     */
    public boolean hasErrors() {
        return !fieldErrors.isEmpty() || !formErrors.isEmpty();
    }
}
