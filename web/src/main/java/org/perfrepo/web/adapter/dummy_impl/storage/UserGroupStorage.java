package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.user.GroupDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary in-memory user and group storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UserGroupStorage {

    private Long key = 1l;
    private List<GroupDto> data = new ArrayList<>();

    public GroupDto getById(Long id) {
        return data.stream().filter(dto -> dto.getId().equals(id)).findFirst().get();
    }

    public GroupDto create(GroupDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public GroupDto update(GroupDto dto) {
        boolean removed = data.removeIf(userGroup -> userGroup.getId().equals(dto.getId()));

        if(removed){
            data.add(dto);
        }else {
            return null;
        }

        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(userGroup -> userGroup.getId().equals(id));
    }

    public List<GroupDto> getAll(){
        return data;
    }

    private Long getNextId() {
        return key++;
    }
}

