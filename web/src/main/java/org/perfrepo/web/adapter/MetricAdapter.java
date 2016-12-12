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
    MetricDto getMetric(Long id);

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
     * Add a metric to existing test. If a metric does not exist, it will be created.
     *
     * @param metric Metric {@link org.perfrepo.dto.metric.MetricDto} that will be added to the test.
     * @param testId Test {@link org.perfrepo.dto.test.TestDto} identifier.
     * @return Metric {@link MetricDto} that was added to test.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto addMetric(MetricDto metric, Long testId);

    /**
     * Remove a metric from existing test.
     *
     * @param metricId Identifier of {@link org.perfrepo.dto.metric.MetricDto} metric that will be removed.
     * @param testId Test {@link org.perfrepo.dto.test.TestDto} identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeMetric(Long metricId, Long testId);

    /**
     * Return all stored {@link MetricDto} objects.
     *
     * @return List of all stored {@link MetricDto} objects.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    List<MetricDto> getAllMetrics();
}
