package org.perfrepo.web.service.exceptions;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class DuplicateEntityException extends ServiceException {

    public DuplicateEntityException(String keyToResourceBundle, Throwable cause, String... params) {
        super(keyToResourceBundle, cause, params);
    }

    public DuplicateEntityException(String keyToResourceBundle, String... params) {
        super(keyToResourceBundle, params);
    }
}
