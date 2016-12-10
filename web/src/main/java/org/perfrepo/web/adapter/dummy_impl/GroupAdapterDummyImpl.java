package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.web.adapter.GroupAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.List;

/**
 * Temporary implementation of {@link GroupAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class GroupAdapterDummyImpl implements GroupAdapter {

    @Inject
    private Storage storage;

    @Override
    public List<GroupDto> getAllGroups() {
        return storage.group().getAll();
    }
}
