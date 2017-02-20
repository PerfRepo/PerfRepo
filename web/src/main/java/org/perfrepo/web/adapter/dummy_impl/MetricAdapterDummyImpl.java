package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.MetricAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.ValidationException;

import javax.inject.Inject;
import java.util.ArrayList;
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
        validate(metric, true);

        MetricDto origin = storage.metric().getById(metric.getId());
        if (origin == null) {
            throw new NotFoundException("Metric does not exist.");
        }
        // metric name was changed
        List<TestDto> tests = new ArrayList<>();
        if (!metric.getName().equals(origin.getName())) {
            storage.test().getAll().forEach(test -> {
                if (test.getMetrics().remove(origin)) {
                    tests.add(test);
                }
            });
        }
        // update existing metric
        storage.metric().update(metric);

        tests.forEach(test -> {
            test.getMetrics().add(origin);
        });

        return origin;
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

        validate(metric, false);

        MetricDto metricToAdd = storage.metric().getByName(metric.getName());

        if (metricToAdd == null) {
            metricToAdd = storage.metric().create(metric);
        }

        if (!test.getMetrics().contains(metricToAdd)) {
            storage.test().addMetric(test, metricToAdd);
        }

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

    private void validate(MetricDto metric, boolean edit) {
        ValidationErrors validation = new ValidationErrors();

        // metric name
        if (metric.getName() == null) {
            validation.addFieldError("name", "Metric name is a required field");
        } else if (metric.getName().trim().length() < 3) {
            validation.addFieldError("name", "Metric name must be at least three characters.");
        } else if (edit) {
            MetricDto existing = storage.metric().getByName(metric.getName());
            if (existing != null && !existing.getId().equals(metric.getId())) {
                validation.addFieldError("name", "Metric with this name already exists.");
            }
        }

        // metric comparator
        if (metric.getComparator() == null) {
            validation.addFieldError("comparator", "Metric comparator is a required field.");
        }

        // metric description
        if (metric.getDescription() != null && metric.getDescription().length() > 500) {
            validation.addFieldError("description", "Metric description must not be more than 500 characters.");
        }

        if (validation.hasFieldErrors()) {
            throw new ValidationException("Metric contains validation errors, please fix it.", validation);
        }
    }
}
