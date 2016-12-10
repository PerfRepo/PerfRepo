package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.MetricAdapter;
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

        if (metric == null) {
            throw new NotFoundException("Metric does not exist.");
        }

        return metric;
    }

    @Override
    public MetricDto updateMetric(MetricDto metric) {
        return storage.metric().update(metric);
    }

    @Override
    public List<MetricDto> getAllMetrics() {
        return storage.metric().getAll();
    }
}
