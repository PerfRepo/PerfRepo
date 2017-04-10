package org.perfrepo.client;

import org.perfrepo.dto.group.GroupDto;

import java.util.List;

/**
 * Created by Adam Krajcik on 10.04.2017.
 */
public class GroupClient {

    private PerfRepoClient parentClient;

    public GroupClient(PerfRepoClient parentClient) { this.parentClient = parentClient; }

    public List<GroupDto> getAll() {
        return null;
    }
}
