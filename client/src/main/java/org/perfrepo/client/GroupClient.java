package org.perfrepo.client;

import org.perfrepo.dto.group.GroupDto;

import java.util.Collection;

/**
 * Created by UCTOVNIK on 10.04.2017.
 */
public class GroupClient {

    private PerfRepoClient parentClient;

    public GroupClient(PerfRepoClient parentClient) { this.parentClient = parentClient; }

    public Collection<GroupDto> getAll() {
        return null;
    }
}
