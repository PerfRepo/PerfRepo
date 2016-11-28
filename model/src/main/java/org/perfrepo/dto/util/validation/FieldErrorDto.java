package org.perfrepo.dto.util.validation;

/**
 * Represents validation error for specified field name.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class FieldErrorDto {

    private String field;

    private String message;

    /**
     *
     *
     * @param field Field name of validation item.
     * @param message Subscribing validation error.
     */
    public FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }

    /**
     * Returns the field name of validation item.
     * @return Unique field.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the message of validation error.
     * @return Message.
     */
    public String getMessage() {
        return message;
    }
}
