package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test_execution.ValueDto;
import org.perfrepo.dto.test_execution.ValuesGroupDto;
import org.perfrepo.enums.MeasuredValueType;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValuesGroupDtoBuilder {

    private ValuesGroupDto valuesGroupDto;

    public ValuesGroupDtoBuilder() {
        valuesGroupDto = new ValuesGroupDto();
        valuesGroupDto.setValues(new ArrayList<>());
    }

    public ValuesGroupDtoBuilder metric(MetricDto metric) {
        valuesGroupDto.setMetricId(metric.getId());
        return this;
    }

    public ValuesGroupDtoBuilder valueType(MeasuredValueType valueType) {
        valuesGroupDto.setValueType(valueType);
        return this;
    }

    public ValuesGroupDtoBuilder value(ValueDto value) {
        valuesGroupDto.getValues().add(value);
        return this;
    }

    public ValuesGroupDtoBuilder parameterNames(String... parameterNames) {
        valuesGroupDto.setParameterNames(Stream.of(parameterNames).collect(Collectors.toSet()));
        return this;
    }

    public ValuesGroupDto build() {
        return valuesGroupDto;
    }
}