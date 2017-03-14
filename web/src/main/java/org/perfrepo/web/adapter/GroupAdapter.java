package org.perfrepo.web.adapter;

import org.perfrepo.dto.group.GroupDto;

import java.util.List;

/**
 * Service adapter for user groups. Adapter provides operations for {@link GroupDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface GroupAdapter {
    /**
     * Return all user groups.
     *
     * @return List of all user groups.
     */
    List<GroupDto> getAllGroups();
}
