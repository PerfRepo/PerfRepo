package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.web.adapter.UserAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;

/**
 * Temporary implementation of {@link UserAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UserAdapterDummyImpl implements UserAdapter {

    @Inject
    private Storage storage;
}
