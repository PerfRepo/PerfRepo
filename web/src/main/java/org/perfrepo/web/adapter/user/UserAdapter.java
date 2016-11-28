package org.perfrepo.web.adapter.user;

import org.perfrepo.dto.user.GroupDto;

import java.util.List;

/**
 * Service adapter for {@link org.perfrepo.dto.user.UserDto} and {@link GroupDto} object. Adapter supports CRUD and another operations over these objects.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface UserAdapter {

    List<GroupDto> getAllGroups();
    // TODO
}
