package org.perfrepo.web.adapter;

import org.perfrepo.dto.metric.MetricDto;

import java.util.List;

/**
 * Service adapter for test metrics. Adapter provides operations for
 * {@link MetricDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface MetricAdapter {
    /**
     * Return {@link MetricDto} object by its id.
     *
     * @param id The metric identifier.
     *
     * @return Found {@link MetricDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto getMetric(Long id);

    /**
     * Update the {@link MetricDto} object.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Metric name</li>
     *  <li><strong>description</strong> - Metric description</li>
     *  <li><strong>comparator</strong> - Metric comparator</li>
     * <ul/>
     *
     * @param metric Parameters of the metric that will be updated.
     *
     * @return Updated {@link MetricDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto updateMetric(MetricDto metric);

    /**
     * Add the metric to the test. If the metric does not exist, it will be created.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Metric name</li>
     *  <li><strong>description</strong> - Metric description</li>
     *  <li><strong>comparator</strong> - Metric comparator</li>
     * <ul/>
     *
     * @param metric The metric {@link MetricDto} that will be added to the test.
     * @param testId The test {@link org.perfrepo.dto.test.TestDto} identifier.
     *
     * @return The metric {@link MetricDto} that was added to the test.
     *
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto addMetric(MetricDto metric, Long testId);

    /**
     * Remove the metric from the test. If the metric is not assigned to any test, it will be deleted.
     *
     * @param metricId The identifier of {@link MetricDto} the metric that will be removed.
     * @param testId Test {@link org.perfrepo.dto.test.TestDto} identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeMetric(Long metricId, Long testId);

    /**
     * Return all stored {@link MetricDto} objects.
     *
     * @return The list of all stored {@link MetricDto} objects.
     *
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    List<MetricDto> getAllMetrics();
}
