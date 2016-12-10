package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.user.UserDto;

import java.util.*;

/**
 * Temporary in-memory user storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UserStorage {

    private Long key = 1L;
    private List<UserDto> data = new ArrayList<>();


    public UserDto getById(Long id) {
        UserDto user = getByIdWithPassword(id);

        if (user != null) {
            UserDto copy = copyUser(user);
            copy.setPassword(null);
            return copy;
        }

        return null;
    }

    public UserDto getByIdWithPassword(Long id) {
        Optional<UserDto> user = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return user.isPresent() ? user.get() : null;
    }

    public UserDto getByUsername(String username) {
        UserDto user = getByUsernameWithPassword(username);

        if (user != null) {
            UserDto copy = copyUser(user);
            copy.setPassword(null);
            return copy;
        }

        return null;
    }

    public UserDto getByUsernameWithPassword(String username) {
        Optional<UserDto> user = data.stream().filter(dto -> dto.getUsername().equals(username)).findFirst();
        return user.isPresent() ? user.get() : null;
    }

    public UserDto create(UserDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public UserDto update(UserDto dto) {
        boolean removed = data.removeIf(user -> user.getId().equals(dto.getId()));

        if (removed) {
            data.add(dto);
        } else {
            return null;
        }

        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(user -> user.getId().equals(id));
    }

    public List<UserDto> getAll() {
        return data;
    }

    private Long getNextId() {
        return key++;
    }

    private UserDto copyUser(UserDto user) {
        UserDto userCopy = new UserDto();
        userCopy.setId(user.getId());
        userCopy.setUsername(user.getUsername());
        userCopy.setFirstName(user.getFirstName());
        userCopy.setPassword(user.getPassword());
        userCopy.setLastName(user.getLastName());
        userCopy.setEmail(user.getEmail());
        return userCopy;
    }


}

