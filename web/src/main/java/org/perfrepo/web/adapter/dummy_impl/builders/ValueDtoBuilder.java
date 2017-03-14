package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.test_execution.ValueDto;
import org.perfrepo.dto.test_execution.ValueParameterDto;

import java.util.HashSet;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueDtoBuilder {

    private ValueDto valueDto;

    public ValueDtoBuilder() {
        valueDto = new ValueDto();
        valueDto.setParameters(new HashSet<>());
    }

    public ValueDtoBuilder value(double value) {
        valueDto.setValue(value);
        return this;
    }

    public ValueDtoBuilder parameter(String name, double value) {
        ValueParameterDto parameterDto = new ValueParameterDto();
        parameterDto.setName(name);
        parameterDto.setValue(value);
        valueDto.getParameters().add(parameterDto);
        return this;
    }

    public ValueDto build() {
        return valueDto;
    }
}
