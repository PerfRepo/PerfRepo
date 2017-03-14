package org.perfrepo.dto.util.validation;

/**
 * Represents validation error for specified name name.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class FieldError {

    private String name;

    private String message;

    /**
     *
     * @param name Field name of validation item.
     * @param message Subscribing validation error.
     */
    public FieldError(String name, String message) {
        this.name = name;
        this.message = message;
    }

    /**
     * Returns the field name of validation item.
     * @return Unique name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the message of validation error.
     * @return Message.
     */
    public String getMessage() {
        return message;
    }
}
