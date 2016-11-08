package org.perfrepo.web.service.model_mapper;

import org.perfrepo.model.Test;
import org.perfrepo.web.dto.TestDto;
import javax.inject.Inject;

public class TestModelMapper implements AbstractModelMapper<Test, TestDto> {

    @Inject
    private MetricModelMapper metricModelMapper;

    @Override
    public TestDto convertToDto(Test entityObject) {
        if(entityObject == null){
            return null;
        }

        TestDto dto = new TestDto();
        dto.setId(entityObject.getId());
        dto.setName(entityObject.getName());
        dto.setUid(entityObject.getUid());
        dto.setGroupId(entityObject.getGroupId());
        dto.setDescription(entityObject.getDescription());
        dto.setMetrics(metricModelMapper.convertToDtoList(entityObject.getMetrics()));

        return dto;
    }

    @Override
    public Test convertToEntity(TestDto dtoObject) {
        if(dtoObject == null){
            return null;
        }

        Test entity = new Test();
        entity.setId(dtoObject.getId());
        entity.setName(dtoObject.getName());
        entity.setUid(dtoObject.getUid());
        entity.setGroupId(dtoObject.getGroupId());
        entity.setDescription(dtoObject.getDescription());
        entity.setMetrics(metricModelMapper.convertToEntityList(dtoObject.getMetrics()));
        return entity;
    }
}
