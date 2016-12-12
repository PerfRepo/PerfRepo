package org.perfrepo.web.adapter;

import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchParams;

import java.util.List;

/**
 * Service adapter for {@link TestDto} object. Adapter supports CRUD and another operations over this object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface TestAdapter {

    /**
     * Return {@link TestDto} object by its id.
     *
     * @param id The test identifier.
     * @return Found {@link TestDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestDto getTest(Long id);

    /**
     * Return {@link TestDto} object by its uid.
     *
     * @param uid The test string unique identifier.
     * @return Found {@link TestDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestDto getTest(String uid);

    /**
     * Create new {@link TestDto} object.
     *
     * @param test Parameters of the test that will be created.
     * @return Created {@link TestDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestDto createTest(TestDto test);

    /**
     * Update a {@link TestDto} object.
     *
     * @param test Parameters of the test that will be updated.
     * @return Updated {@link TestDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestDto updateTest(TestDto test);

    /**
     * Remove a {@link TestDto} object.
     *
     * @param id The test identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeTest(Long id);

    /**
     * Return all tests.
     *
     * @return List of all tests.
     */
    List<TestDto> getAllTests();

    /**
     * Return all {@link TestDto} tests that satisfy search conditions.
     *
     * @return List of {@link TestDto} tests.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    SearchResult<TestDto> searchTests(TestSearchParams searchParams);

}
