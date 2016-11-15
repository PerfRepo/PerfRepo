package org.perfrepo.web.front_api.storage;

import org.perfrepo.web.dto.UserGroupDto;

import java.util.ArrayList;
import java.util.List;

public class UserGroupStorage {

    private Long key = 1l;
    private List<UserGroupDto> data = new ArrayList<>();

    public UserGroupDto getById(Long id) {
        return data.stream().filter(dto -> dto.getId().equals(id)).findFirst().get();
    }

    public UserGroupDto create(UserGroupDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public UserGroupDto update(UserGroupDto dto) {
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

    public List<UserGroupDto> getAll(){
        return data;
    }

    private Long getNextId() {
        return key++;
    }

}

