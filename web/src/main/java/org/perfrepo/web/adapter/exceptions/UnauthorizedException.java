package org.perfrepo.web.adapter.exceptions;

import org.perfrepo.dto.util.validation.ValidationErrors;

/**
 * This unchecked exception is thrown if service adapter could not perform action,
 * because the user has not permission to perform the action.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UnauthorizedException extends ValidationException {

    /**
     * Constructs a {@link UnauthorizedException} with the specified detail message.
     *
     * @param message Detailed message.
     */
    public UnauthorizedException(String message, ValidationErrors validationErrors) {
        super(message, validationErrors);
    }
}
