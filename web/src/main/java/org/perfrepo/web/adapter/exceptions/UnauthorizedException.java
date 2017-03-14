package org.perfrepo.web.adapter.exceptions;

/**
 * This unchecked exception is thrown if service adapter could not perform action,
 * because the user has not permission to perform the action.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UnauthorizedException extends AdapterException {

    /**
     * Constructs a {@link UnauthorizedException} with the specified detail message.
     *
     * @param message Detailed message.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
