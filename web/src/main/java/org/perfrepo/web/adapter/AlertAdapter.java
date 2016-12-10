package org.perfrepo.web.adapter;

import org.perfrepo.dto.alert.AlertDto;

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
    AlertDto getAlertById(Long id);

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
     * Delete the {@link AlertDto} test alert.
     *
     * @param id The test alert identifier.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test alert does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void deleteAlert(Long id);

    /**
     * Return all test alerts for specific test.
     *
     * @return List of test alerts.
     */
    List<AlertDto> getAllAlertsForTest(Long testId);
}
