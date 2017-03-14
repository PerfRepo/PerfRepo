package org.perfrepo.web.adapter.exceptions;

/**
 * This unchecked exception is thrown if the request is bad (wrong input params, checked error).
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BadRequestException extends AdapterException {

    /**
     * Constructs a {@link BadRequestException} with the specified detail message.
     *
     * @param message Detailed message.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
