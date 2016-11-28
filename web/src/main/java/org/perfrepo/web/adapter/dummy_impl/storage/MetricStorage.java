package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.metric.MetricDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary in-memory metric storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricStorage {

    private Long key = 1l;
    private List<MetricDto> data = new ArrayList<>();

    public MetricDto getById(Long id) {
        return data.stream().filter(dto -> dto.getId().equals(id)).findFirst().get();
    }

    public MetricDto create(MetricDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public MetricDto update(MetricDto dto) {
        boolean removed = data.removeIf(metric -> metric.getId().equals(dto.getId()));

        if(removed){
            data.add(dto);
        }else {
            return null;
        }

        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(metric -> metric.getId().equals(id));
    }

    public List<MetricDto> getAll(){
        return data;
    }

    private Long getNextId() {
        return key++;
    }
}

