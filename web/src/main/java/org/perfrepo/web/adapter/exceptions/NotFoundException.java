package org.perfrepo.web.adapter.exceptions;

/**
 * This unchecked exception is thrown if service adapter could not find requested resource.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class NotFoundException extends AdapterException {

    /**
     * Constructs a {@link NotFoundException} with the specified detail message.
     *
     * @param message Detailed message.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
