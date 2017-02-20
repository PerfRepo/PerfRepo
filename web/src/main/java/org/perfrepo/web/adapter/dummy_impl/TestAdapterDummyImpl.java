package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchCriteria;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.TestAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.ValidationException;

import javax.inject.Inject;
import java.util.LinkedHashSet;
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
    public TestDto getTest(Long id) {
        TestDto test = storage.test().getById(id);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        return test;
    }

    @Override
    public TestDto getTest(String uid) {
        TestDto test = storage.test().getByUid(uid);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        return test;
    }

    @Override
    public TestDto createTest(TestDto test) {
        validate(test);
        // it is not possible to set it, metrics and alerts can be added in test detail
        test.setMetrics(new LinkedHashSet<>());
        test.setAlerts(new LinkedHashSet<>());

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

       validate(test);

        test.setAlerts(originTest.getAlerts());
        test.setMetrics(originTest.getMetrics());

        return storage.test().update(test);
    }

    @Override
    public void removeTest(Long id) {
        TestDto test = storage.test().getById(id);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        // remove alerts
        Set<AlertDto> alerts = test.getAlerts();
        if (alerts != null) {
            alerts.forEach(alert -> storage.alert().delete(alert.getId()));
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
    public SearchResult<TestDto> searchTests(TestSearchCriteria searchParams) {
        // delay...
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return storage.test().search(searchParams);
    }

    @Override
    public boolean isSubscriber(Long testId) {
        TestDto test = storage.test().getById(testId);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        // TODO, logged user
        Long userId = storage.user().getByUsername("grunwjir").getId();
        return storage.testToAlert().contains(testId, userId);
    }

    @Override
    public void addSubscriber(Long testId) {
        TestDto test = storage.test().getById(testId);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        // delay...
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO, logged user
        Long userId = storage.user().getByUsername("grunwjir").getId();
        storage.testToAlert().add(testId, userId);
    }

    @Override
    public void removeSubscriber(Long testId) {
        TestDto test = storage.test().getById(testId);

        if (test == null) {
            throw new NotFoundException("Test does not exist.");
        }

        // delay...
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO, logged user
        Long userId = storage.user().getByUsername("grunwjir").getId();
        storage.testToAlert().remove(testId, userId);
    }

    private void validate(TestDto test) {
        ValidationErrors validation = new ValidationErrors();

        // test name
        if (test.getName() == null) {
            validation.addFieldError("name", "Test name is a required field");
        } else if (test.getName().trim().length() < 3) {
            validation.addFieldError("name", "Test name must be at least three characters.");
        }

        // test uid
        if (test.getUid() == null) {
            validation.addFieldError("uid", "Test uid is a required field");
        } else if (test.getUid().trim().length() < 3) {
            validation.addFieldError("uid", "Test uid must be at least three characters.");
        } else {
            TestDto existing = storage.test().getByUid(test.getUid());
            if (existing != null && !existing.getId().equals(test.getId())) {
                validation.addFieldError("uid", "Test with this UID already exists.");
            }
        }

        // test group
        if (test.getGroup() == null) {
            validation.addFieldError("group", "Test group is a required field");
        }

        // test description
        if (test.getDescription() != null && test.getDescription().length() > 500) {
            validation.addFieldError("description", "Test description must not be more than 500 characters.");
        }

        if (validation.hasFieldErrors()) {
            throw new ValidationException("Test contains validation errors, please fix it.", validation);
        }
    }
}
