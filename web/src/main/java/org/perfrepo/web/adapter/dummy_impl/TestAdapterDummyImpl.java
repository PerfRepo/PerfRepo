package org.perfrepo.web.adapter.dummy_impl;

import org.apache.commons.lang.NotImplementedException;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.util.validation.FieldError;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchParams;
import org.perfrepo.dto.user.UserDto;
import org.perfrepo.dto.util.validation.ValidationError;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;
import org.perfrepo.web.adapter.TestAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

/**
 * Temporary implementation of {@link TestAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestAdapterDummyImpl implements TestAdapter {

    @Inject
    private Storage storage;

    @Override
    public TestDto getTestById(Long id) {
        TestDto test = storage.test().getById(id);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        return test;
    }

    @Override
    public TestDto getTestByUid(String uid) {
        TestDto test = storage.test().getByUid(uid);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        return test;
    }

    @Override
    public TestDto createTest(TestDto test) {
        // TODO validation
        // it is not possible to set it, metrics and alerts can be added in test detail
        test.setMetrics(null);
        test.setAlerts(null);

        return storage.test().create(test);
    }

    @Override
    public TestDto updateTest(TestDto test) {
        if (test == null) {
            throw new BadRequestException("Bad input data.");
        }

        if (test.getId() == null) {
            throw new NotFoundException("Test does not exist.");
        }

        TestDto originTest = storage.test().getById(test.getId());

        if (originTest == null) {
            throw new NotFoundException("Test does not exist.");
        }

        // TODO validation

        test.setAlerts(originTest.getAlerts());
        test.setMetrics(originTest.getMetrics());

        return storage.test().update(test);
    }

    @Override
    public void deleteTest(Long id) {
        TestDto test = storage.test().getById(id);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        boolean removed = storage.test().delete(test.getId());

        if (!removed) {
            throw new AdapterException("You can not delete test.");
        }
    }

    @Override
    public List<TestDto> getAllTests() {
        return storage.test().getAll();
    }

    @Override
    public SearchResult<TestDto> searchTests(TestSearchParams searchParams) {
        return storage.test().search(searchParams);
    }

    @Override
    public MetricDto addMetricToTest(MetricDto metric, Long testId) {
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
    public void removeMetricFromTest(Long metricId, Long testId) {
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
    public void subscribeToTestAlerts(Long alertId, Long userId) {
        throw new NotImplementedException();
    }

    @Override
    public void unsubscribeFromTestAlerts(Long alertId, Long userId) {
        throw new NotImplementedException();
    }

    @Override
    public AlertDto createTestAlert(Long testId, AlertDto alert) {
        throw new NotImplementedException();
    }

    @Override
    public AlertDto updateTestAlert(Long testId, AlertDto alert) {
        throw new NotImplementedException();
    }

    @Override
    public void deleteTestAlert(Long testId, Long alertId) {
        throw new NotImplementedException();
    }

    @Override
    public List<UserDto> getSubscribedUsersToTestAlerts(Long testId) {
        throw new NotImplementedException();
    }
}
