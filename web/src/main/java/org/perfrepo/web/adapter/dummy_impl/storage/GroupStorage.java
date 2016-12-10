package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.group.GroupDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Temporary in-memory group storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class GroupStorage {

    private Long key = 1L;
    private List<GroupDto> data = new ArrayList<>();

    public GroupDto getById(Long id) {
        Optional<GroupDto> group = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return group.isPresent() ? group.get() : null;
    }

    public GroupDto create(GroupDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public GroupDto update(GroupDto dto) {
        boolean removed = data.removeIf(userGroup -> userGroup.getId().equals(dto.getId()));

        if (removed) {
            data.add(dto);
        } else {
            return null;
        }

        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(userGroup -> userGroup.getId().equals(id));
    }

    public List<GroupDto> getAll() {
        return data;
    }

    private Long getNextId() {
        return key++;
    }
}

