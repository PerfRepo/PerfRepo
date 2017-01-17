package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.alert.AlertDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Temporary in-memory alert storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AlertStorage {

    private Long key = 1L;
    private List<AlertDto> data = new ArrayList<>();

    public AlertDto getById(Long id) {
        Optional<AlertDto> alert = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return alert.isPresent() ? alert.get() : null;
    }

    public AlertDto create(AlertDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public AlertDto update(AlertDto dto) {
        AlertDto alert = getById(dto.getId());

        if (alert != null) {
            alert.setName(dto.getName());
            alert.setTags(dto.getTags());
            alert.setDescription(dto.getDescription());
            alert.setMetric(dto.getMetric());
            alert.setCondition(dto.getCondition());
            alert.setLinks(dto.getLinks());
            return alert;
        } else {
            return null;
        }
    }

    public boolean delete(Long id) {
        return data.removeIf(alert -> alert.getId().equals(id));
    }

    public List<AlertDto> getAll() {
        return data;
    }

    private Long getNextId() {
        return key++;
    }
}

