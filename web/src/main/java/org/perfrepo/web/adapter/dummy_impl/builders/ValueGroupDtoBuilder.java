package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test_execution.ValueDto;
import org.perfrepo.dto.test_execution.ValueGroupDto;

import java.util.ArrayList;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueGroupDtoBuilder {

    private ValueGroupDto valueGroupDto;

    public ValueGroupDtoBuilder() {
        valueGroupDto = new ValueGroupDto();
        valueGroupDto.setValues(new ArrayList<>());
    }

    public ValueGroupDtoBuilder metric(MetricDto metric) {
        valueGroupDto.setMetricId(metric.getId());
        return this;
    }

    public ValueGroupDtoBuilder value(ValueDto value) {
        valueGroupDto.getValues().add(value);
        return this;
    }

    public ValueGroupDto build() {
        return valueGroupDto;
    }
}
