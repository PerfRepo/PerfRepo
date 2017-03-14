package org.perfrepo.web.adapter;

import org.perfrepo.dto.user.UserDto;

import java.util.List;

/**
 * Service adapter for users. Adapter provides operations for {@link UserDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface UserAdapter {
    /**
     * Return all users.
     *
     * @return The list of all users.
     */
    List<UserDto> getAllUsers();
}
