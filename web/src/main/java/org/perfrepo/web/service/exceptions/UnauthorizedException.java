package org.perfrepo.web.service.exceptions;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UnauthorizedException extends ServiceException {

    public UnauthorizedException(String keyToResourceBundle, Throwable cause, String... params) {
        super(keyToResourceBundle, cause, params);
    }

    public UnauthorizedException(String keyToResourceBundle, String... params) {
        super(keyToResourceBundle, params);
    }
}
