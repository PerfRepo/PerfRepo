package org.perfrepo.web.adapter;

import org.perfrepo.dto.group.GroupDto;

import java.util.List;

/**
 * Service adapter for {@link org.perfrepo.dto.group.GroupDto} object. Adapter supports CRUD and another
 * operations over this object. TODO: adapter is not complete
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
