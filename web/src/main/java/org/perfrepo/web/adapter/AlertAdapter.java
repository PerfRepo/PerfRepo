package org.perfrepo.web.adapter;

import org.perfrepo.dto.alert.AlertDto;

import java.util.List;

/**
 * Service adapter for test alerts. Adapter provides operations for {@link AlertDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface AlertAdapter {

    /**
     * Return the {@link AlertDto} test alert by its id.
     *
     * @param id The alert identifier.
     *
     * @return The found {@link AlertDto} test alert.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the test alert does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AlertDto getAlert(Long id);

    /**
     * Create new {@link AlertDto} test alert.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Alert name</li>
     *  <li><strong>description</strong> - Alert description</li>
     *  <li><strong>condition</strong> - Alert condition</li>
     *  <li><strong>links</strong> - Links</li>
     *  <li><strong>metric</strong> - Selected metric</li>
     *  <li><strong>tags</strong> - Tags</li>
     * <ul/>
     *
     * @param alert Parameters of the test alert that will be created.
     *
     * @return The created {@link AlertDto} test alert.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AlertDto createAlert(AlertDto alert);

    /**
     * Update the {@link AlertDto} test alert.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Alert name</li>
     *  <li><strong>description</strong> - Alert description</li>
     *  <li><strong>condition</strong> - Alert condition</li>
     *  <li><strong>links</strong> - Links</li>
     *  <li><strong>metric</strong> - Selected metric</li>
     *  <li><strong>tags</strong> - Tags</li>
     * <ul/>
     *
     * @param alert Parameters of the test alert that will be updated.
     *
     * @return Updated test alert.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AlertDto updateAlert(AlertDto alert);

    /**
     * Remove the {@link AlertDto} test alert.
     *
     * @param id The test alert identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeAlert(Long id);

    /**
     * Return all test alerts for specific test.
     *
     * @param testId The test identifier.
     *
     * @return List of test alerts.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    List<AlertDto> getAllAlertsForTest(Long testId);
}
