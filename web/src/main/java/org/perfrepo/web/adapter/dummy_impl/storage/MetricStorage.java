package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.metric.MetricDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Temporary in-memory metric storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricStorage {

    private Long key = 1L;
    private List<MetricDto> data = new ArrayList<>();

    public MetricDto getById(Long id) {
        Optional<MetricDto> metric = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return metric.isPresent() ? metric.get() : null;
    }

    public MetricDto getByName(String name) {
        Optional<MetricDto> metric = data.stream().filter(dto -> dto.getName().equals(name)).findFirst();
        return metric.isPresent() ? metric.get() : null;
    }

    public MetricDto create(MetricDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public MetricDto update(MetricDto dto) {
        boolean removed = data.removeIf(metric -> metric.getId().equals(dto.getId()));

        if (removed) {
            data.add(dto);
        } else {
            return null;
        }

        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(metric -> metric.getId().equals(id));
    }

    public List<MetricDto> getAll() {
        return data;
    }

    private Long getNextId() {
        return key++;
    }
}

