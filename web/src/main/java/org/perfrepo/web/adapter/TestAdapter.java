package org.perfrepo.web.adapter;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchParams;
import org.perfrepo.dto.user.UserDto;

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
    TestDto getTestById(Long id);

    /**
     * Return {@link TestDto} object by its uid.
     *
     * @param uid The test string unique identifier.
     * @return Found {@link TestDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestDto getTestByUid(String uid);

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
     * Delete a {@link TestDto} object.
     *
     * @param id The test identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void deleteTest(Long id);

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
    SearchResult searchTests(TestSearchParams searchParams);

    /**
     * Add a metric to existing test. If a metric does not exist, it will be created.
     *
     * @param metric Metric {@link org.perfrepo.dto.metric.MetricDto} that will be added to the test.
     * @param testId Test {@link TestDto} identifier.
     * @return Metric {@link MetricDto} that was added to test.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    MetricDto addMetricToTest(MetricDto metric, Long testId);

    /**
     * Remove a metric from existing test.
     *
     * @param metricId Identifier of {@link org.perfrepo.dto.metric.MetricDto} metric that will be removed.
     * @param testId Test {@link TestDto} identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeMetricFromTest(Long metricId, Long testId);

    /**
     * Subscribe the user to the test alerts.
     *
     * @param testId Test {@link TestDto} identifier.
     * @param userId User {@link UserDto} identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test or user does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void subscribeToTestAlerts(Long testId, Long userId);

    /**
     * Unsubscribe the user from the test alerts.
     *
     * @param testId Test {@link TestDto} identifier.
     * @param userId User {@link UserDto} identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test or user does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void unsubscribeFromTestAlerts(Long testId, Long userId);

    /**
     * Create new {@link AlertDto} alert for the test.
     *
     * @param testId Test {@link TestDto} identifier.
     * @param alert
     * @return
     */
    AlertDto createTestAlert(Long testId, AlertDto alert);

    /**
     * Update the {@link AlertDto} alert of the test.
     *
     * @param testId Test {@link TestDto} identifier.
     * @param alert Alert {@link AlertDto} parameters.
     * @return
     */
    AlertDto updateTestAlert(Long testId, AlertDto alert);

    /**
     * Delete {@link AlertDto} alert from the test.
     *
     * @param testId Test {@link TestDto} identifier.
     * @param alertId Alert {@link AlertDto} identifier.
     */
    void deleteTestAlert(Long testId, Long alertId);

    /**
     * Return subscribed users to test alerts.
     *
     * @param testId Test {@link TestDto} identifier.
     * @return List of subscribed users to the test.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    List<UserDto> getSubscribedUsersToTestAlerts(Long testId);

}
