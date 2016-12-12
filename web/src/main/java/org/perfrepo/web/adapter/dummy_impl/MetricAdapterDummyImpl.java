package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
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
    public MetricDto getMetric(Long id) {
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
    public MetricDto addMetric(MetricDto metric, Long testId) {
        if (metric  == null) {
            throw new BadRequestException("Bad input data for the metric.");
        }

        TestDto test = storage.test().getById(testId);
        if (test == null) {
            throw new BadRequestException("Test does not exist.");
        }

        // TODO validate metric

        MetricDto metricToAdd = storage.metric().getByName(metric.getName());
        if (metricToAdd == null) {
            metricToAdd = storage.metric().create(metric);
        }

        storage.test().addMetric(test, metricToAdd);

        return metricToAdd;
    }

    @Override
    public void removeMetric(Long metricId, Long testId) {
        TestDto test = storage.test().getById(testId);
        MetricDto metric = storage.metric().getById(metricId);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        if (metric == null) {
            throw new NotFoundException("Metric does not exist.");
        }

        storage.test().removeMetric(test, metric);

        // remove metric from storage if it is not used
        List<TestDto> tests = storage.test().getAll();
        boolean unusedMetric = tests.stream().noneMatch(t -> t.getMetrics().contains(metric));
        if (unusedMetric) {
            storage.metric().delete(metric.getId());
        }
    }

    @Override
    public List<MetricDto> getAllMetrics() {
        return storage.metric().getAll();
    }
}
