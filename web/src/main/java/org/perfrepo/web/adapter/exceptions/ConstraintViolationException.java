package org.perfrepo.web.adapter.exceptions;

/**
 * This unchecked exception is thrown when service adapter could not perform action,
 * because it would cause constraint violation.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ConstraintViolationException extends AdapterException {

    /**
     * Constructs a {@link ConstraintViolationException} with the specified detail message.
     *
     * @param message Detailed message.
     */
    public ConstraintViolationException(String message) {
        super(message);
    }
}
