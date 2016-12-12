package org.perfrepo.web.adapter;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.user.UserDto;

import java.util.List;

/**
 * Service adapter for {@link AlertDto} object. Adapter supports CRUD and another
 * operations over this object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface AlertAdapter {

    /**
     * Return {@link AlertDto} test alert by its id.
     *
     * @param id The alert identifier.
     * @return Found {@link AlertDto} test alert.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test alert does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AlertDto getAlert(Long id);

    /**
     * Create new {@link AlertDto} test alert.
     *
     * @param alert Parameters of the test alert that will be created.
     * @return Created {@link AlertDto} test alert.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AlertDto createAlert(AlertDto alert);

    /**
     * Update the {@link AlertDto} test alert.
     *
     * @param alert Parameters of the test alert that will be updated.
     * @return Updated {@link AlertDto} test alert.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test alert does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AlertDto updateAlert(AlertDto alert);

    /**
     * Remove the {@link AlertDto} test alert.
     *
     * @param id The test alert identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test alert does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeAlert(Long id);

    /**
     * Subscribe the logged user to the test alerts.
     *
     * @param testId Test {@link org.perfrepo.dto.test.TestDto} identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test or user does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void subscribeAlerts(Long testId);

    /**
     * Unsubscribe the logged user from the test alerts.
     *
     * @param testId Test {@link org.perfrepo.dto.test.TestDto} identifier.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void unsubscribeAlerts(Long testId);

    /**
     * Return subscribed users to test alerts.
     *
     * @param testId Test {@link org.perfrepo.dto.test.TestDto} identifier.
     * @return List of subscribed users to the test.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    List<UserDto> getAlertSubscribers(Long testId);

    /**
     * Return all test alerts for specific test.
     *
     * @return List of test alerts.
     */
    List<AlertDto> getAllAlertsForTest(Long testId);
}
