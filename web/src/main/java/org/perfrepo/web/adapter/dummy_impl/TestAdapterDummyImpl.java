package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchParams;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.TestAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.List;

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
    public void removeTest(Long id) {
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
}
