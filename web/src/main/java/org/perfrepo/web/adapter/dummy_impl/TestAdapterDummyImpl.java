package org.perfrepo.web.adapter.dummy_impl;

import org.apache.commons.lang.NotImplementedException;
import org.perfrepo.dto.SearchResult;
import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.util.validation.FieldErrorDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchParams;
import org.perfrepo.dto.user.UserDto;
import org.perfrepo.dto.util.validation.ValidationErrorDto;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.ConstraintViolationException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;
import org.perfrepo.web.adapter.test.TestAdapter;
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
public class TestAdapterDummyImpl implements TestAdapter{

    @Inject
    private Storage storage;

    @Override
    public TestDto getTestById(Long id) {
        TestDto test = storage.test().getById(id);

        if(test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        return test;
    }

    @Override
    public TestDto getTestByUid(String uid) {
        TestDto test = storage.test().getByUid(uid);

        if(test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        return test;
    }

    @Override
    public TestDto createTest(TestDto test) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestDto>> constrainViolations = validator.validate(test);

        if(!constrainViolations.isEmpty()) {
            ValidationErrorDto errors = new ValidationErrorDto();

            constrainViolations.forEach(val -> {
                FieldErrorDto error = new FieldErrorDto(val.getPropertyPath().toString(), val.getMessage());
                errors.addFieldError(error);
            });

            throw new ValidationException("Validation error", errors);
        }

        return storage.test().create(test);
    }

    @Override
    public TestDto updateTest(TestDto test) {
        return storage.test().update(test);
    }

    @Override
    public void deleteTest(Long id) {
        TestDto test = storage.test().getById(id);

        if(test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        boolean removed = storage.test().delete(test.getId());

        if(!removed) {
            throw new ConstraintViolationException("You can not delete test.");
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
    public void addMetricToTest(Long testId, Long metricId) {
        throw new NotImplementedException();
    }

    @Override
    public void removeMetricFromTest(Long testId, Long metricId) {
        throw new NotImplementedException();
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
