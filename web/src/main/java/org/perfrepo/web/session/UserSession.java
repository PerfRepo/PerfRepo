package org.perfrepo.web.session;

import org.perfrepo.model.user.User;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface UserSession {

    public User getLoggedUser();
}
