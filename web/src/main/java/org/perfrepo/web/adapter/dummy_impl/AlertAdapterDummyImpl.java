package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.web.adapter.AlertAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;

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
        validate(alert);

        AlertDto createdAlert = storage.alert().create(alert);

        TestDto test = storage.test().getById(createdAlert.getTestId());
        storage.test().addAlert(test, createdAlert);

        return createdAlert;
    }

    @Override
    public AlertDto updateAlert(AlertDto alert) {
        validate(alert);

        AlertDto origin = storage.alert().getById(alert.getId());
        if (origin == null) {
            throw new NotFoundException("Alert does not exist.");
        }

        AlertDto updated = storage.alert().update(alert);

        if (!origin.getTestId().equals(updated.getTestId())) {
            throw new BadRequestException("Change ownership of the alert to another test is not possible.");
        }

        if (!origin.getName().equals(updated.getName())
                || !origin.getCondition().equals(updated.getCondition())) {
            storage.test().getAll().forEach(test -> {
                if (test.getAlerts().remove(origin)) {
                    test.getAlerts().add(updated);
                }
            });

        }

        return updated;
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

    private void validate(AlertDto alert) {
        ValidationErrors validation = new ValidationErrors();

        // alert name
        if (alert.getName() == null) {
            validation.addFieldError("name", "Alert name is a required field");
        } else if (alert.getName().trim().length() < 3) {
            validation.addFieldError("name", "Alert name must be at least three characters.");
        }

        // alert condition
        if (alert.getCondition() == null) {
            validation.addFieldError("condition", "Alert condition is a required field");
        }

        // alert metric
        if (alert.getMetric() == null) {
            validation.addFieldError("metric", "Alert metric is a required field");
        }

        // alert description
        if (alert.getDescription() != null && alert.getDescription().length() > 500) {
            validation.addFieldError("description", "Alert description must not be more than 500 characters.");
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Test contains validation errors, please fix it.", validation);
        }
    }
}
