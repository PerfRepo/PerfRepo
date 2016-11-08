package org.perfrepo.web.service.model_mapper;

import org.perfrepo.model.Entity;
import java.util.Collection;
import java.util.stream.Collectors;

interface AbstractModelMapper<E extends Entity<E>, T> {

    T convertToDto(E entityObject);

    E convertToEntity(T dtoObject);

    default Collection<T> convertToDtoList(Collection<E> entityObjectCollection) {
        if(entityObjectCollection == null) {
            return null;
        }
        return entityObjectCollection.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    default Collection<E> convertToEntityList(Collection<T> dtoObjectCollection) {
        if(dtoObjectCollection == null) {
            return null;
        }
        return dtoObjectCollection.stream().map(this::convertToEntity).collect(Collectors.toList());
    }


}
