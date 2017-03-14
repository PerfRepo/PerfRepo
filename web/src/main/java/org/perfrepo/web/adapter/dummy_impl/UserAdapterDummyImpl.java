package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.user.UserDto;
import org.perfrepo.web.adapter.UserAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.List;

/**
 * Temporary implementation of {@link UserAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UserAdapterDummyImpl implements UserAdapter {

    @Inject
    private Storage storage;

    @Override
    public List<UserDto> getAllUsers() {
        return storage.user().getAll();
    }
}
