package org.perfrepo.web.service.exceptions;

import org.perfrepo.web.util.MessageUtils;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 4888320719223847688L;

    private String keyToResourceBundle;
    private Object[] params;

    /**
     * @param keyToResourceBundle used for GUI
     * @param params
     */
    public UnauthorizedException(String keyToResourceBundle, String... params) {
        this(keyToResourceBundle, null, params);
    }

    /**
     * @param keyToResourceBundle used for GUI
     * @param params
     */
    public UnauthorizedException(String keyToResourceBundle, Throwable cause, String... params) {
        super(MessageUtils.getMessage(keyToResourceBundle, params), cause);
        this.keyToResourceBundle = keyToResourceBundle;
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public String getKeyToResourceBundle() {
        return keyToResourceBundle;
    }
}
