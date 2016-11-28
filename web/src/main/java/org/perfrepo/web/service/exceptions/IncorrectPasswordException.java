package org.perfrepo.web.service.exceptions;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class IncorrectPasswordException extends ServiceException {

    public IncorrectPasswordException(String keyToResourceBundle, Throwable cause, String... params) {
        super(keyToResourceBundle, cause, params);
    }

    public IncorrectPasswordException(String keyToResourceBundle, String... params) {
        super(keyToResourceBundle, params);
    }
}
