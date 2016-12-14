package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.user.UserDto;
import org.perfrepo.web.adapter.AlertAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Temporary implementation of {@link AlertAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AlertAdapterDummyImpl implements AlertAdapter {

    @Inject
    private Storage storage;

    @Override
    public AlertDto getAlert(Long id) {
        AlertDto alert = storage.alert().getById(id);

        if (alert == null) {
            throw new NotFoundException("Alert does not exist.");
        }

        return alert;
    }

    @Override
    public AlertDto createAlert(AlertDto alert) {
        // TODO validate alert

        AlertDto createdAlert = storage.alert().create(alert);

        TestDto test = storage.test().getById(createdAlert.getTestId());
        storage.test().addAlert(test, createdAlert);

        return createdAlert;
    }

    @Override
    public AlertDto updateAlert(AlertDto alert) {
        // TODO validate alert

        AlertDto origin = storage.alert().getById(alert.getId());

        if (!origin.getTestId().equals(alert.getTestId())) {
            throw new BadRequestException("Change ownership of the alert to another test is not possible.");
        }

        if (!origin.getName().equals(alert.getName())
                || !origin.getCondition().equals(alert.getCondition()) ) {
            storage.test().getAll().forEach(test -> {
                if (test.getAlerts().remove(origin)) {
                    test.getAlerts().add(alert);
                }
            });

        }

        return storage.alert().update(alert);
    }

    @Override
    public void removeAlert(Long id) {
        AlertDto alert = storage.alert().getById(id);
        TestDto test = storage.test().getById(alert.getTestId());

        storage.test().removeAlert(test, alert);
        storage.alert().delete(alert.getId());
    }

    @Override
    public List<AlertDto> getAllAlertsForTest(Long testId) {
        TestDto test = storage.test().getById(testId);

        if (test == null) {
            throw new NotFoundException("Test does not exist");
        }

        Set<AlertDto> alerts = test.getAlerts();

        return new ArrayList<>(alerts);
    }

    @Override
    public void subscribeAlerts(Long alertId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsubscribeAlerts(Long alertId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserDto> getAlertSubscribers(Long testId) {
        throw new UnsupportedOperationException();
    }
}
