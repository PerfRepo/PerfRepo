package org.perfrepo.web.adapter;

import org.perfrepo.dto.metric.MetricDto;

import java.util.List;

/**
 * Service adapter for {@link MetricDto} object. Adapter supports CRUD operations over this object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface MetricAdapter {
    /**
     * Return {@link MetricDto} object by its id.
     *
     * @param id The metric identifier.
     * @return Found {@link MetricDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto getMetricById(Long id);

    /**
     * Update the {@link MetricDto} object.
     *
     * @param metric Parameters of the metric that will be updated.
     * @return Updated {@link MetricDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto updateMetric(MetricDto metric);

    /**
     * Return all stored {@link MetricDto} objects.
     *
     * @return List of all stored {@link MetricDto} objects.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    List<MetricDto> getAllMetrics();
}
