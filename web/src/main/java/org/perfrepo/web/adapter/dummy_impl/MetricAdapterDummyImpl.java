package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.ConstraintViolationException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.test.MetricAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.List;

/**
 * Temporary implementation of {@link MetricAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricAdapterDummyImpl implements MetricAdapter {

    @Inject
    private Storage storage;

    @Override
    public MetricDto getMetricById(Long id) {
        MetricDto metric = storage.metric().getById(id);

        if(metric == null) {
            throw new NotFoundException("Metric does not exist.");
        }

        return metric;
    }

    @Override
    public MetricDto createMetric(MetricDto metric) {
        return storage.metric().create(metric);
    }

    @Override
    public MetricDto updateMetric(MetricDto metric) {
        return storage.metric().update(metric);
    }

    @Override
    public void deleteMetric(Long id) {
        MetricDto metric = storage.metric().getById(id);

        if(metric == null) {
            throw new NotFoundException("Metric does not exist.");
        }

        boolean removed = storage.metric().delete(metric.getId());

        if(!removed) {
            throw new ConstraintViolationException("You can not delete metric.");
        }
    }

    @Override
    public List<MetricDto> getAllMetrics() {
        return storage.metric().getAll();
    }
}
