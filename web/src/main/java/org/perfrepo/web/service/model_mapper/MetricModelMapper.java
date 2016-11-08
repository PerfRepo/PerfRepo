package org.perfrepo.web.service.model_mapper;

import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.web.dto.MetricDto;

public class MetricModelMapper implements AbstractModelMapper<Metric, MetricDto> {

    @Override
    public MetricDto convertToDto(Metric entityObject) {
        if (entityObject == null) {
            return null;
        }

        MetricDto dto = new MetricDto();
        dto.setId(entityObject.getId());
        dto.setName(entityObject.getName());
        dto.setDescription(entityObject.getDescription());
        dto.setComparator(entityObject.getComparator().name());
        return dto;
    }

    @Override
    public Metric convertToEntity(MetricDto dtoObject) {
        if(dtoObject == null){
            return null;
        }

        Metric entity = new Metric();
        entity.setId(dtoObject.getId());
        entity.setName(dtoObject.getName());
        entity.setDescription(dtoObject.getDescription());
        entity.setComparator(MetricComparator.valueOf(dtoObject.getComparator())); // TODO Illegal argument exception
        return entity;
    }
}
