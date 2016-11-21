package org.perfrepo.web.service;

import org.perfrepo.model.Alert;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.web.alerting.ConditionChecker;
import org.perfrepo.web.dao.AlertDAO;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TagDAO;
import org.perfrepo.web.dao.TestDAO;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link org.perfrepo.web.service.AlertingService}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AlertingServiceBean implements AlertingService {

   @Inject
   private AlertDAO alertDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private TagDAO tagDAO;

   @Inject
   private AlertingReporterService emailService;

   @Inject
   private ConditionChecker conditionChecker;

   @Inject
   private AlertingReporterService alertingReporterService;

   @Override
   public Alert getAlert(Long id) {
      Alert alert = alertDAO.get(id);
      Collection<Tag> tags = alert.getTags();

      return alert;
   }

   @Override
   public Alert createAlert(Alert alert) {
      makeManaged(alert);
      return alertDAO.create(alert);
   }

   @Override
   public Alert updateAlert(Alert alert) {
      makeManaged(alert);
      return alertDAO.merge(alert);
   }

   @Override
   public void removeAlert(Alert alert) {
      Alert freshAlert = alertDAO.get(alert.getId());
      alertDAO.remove(freshAlert);
   }

   @Override
   public List<Alert> getAlertsList(Test test) {
      List<Alert> alertsList = new ArrayList<>();
      //TODO: solve this
      /*
      if (test != null) {
         alertsList.addAll(test.getAlerts());
      }
      */
      Collections.sort(alertsList, ((o1, o2) -> o1.getName().compareTo(o2.getName())));

      return alertsList;
   }

   @Override
   public void processAlerts(TestExecution testExecution) {
      Test test = testExecution.getTest();
      //TODO: solve this
      //Collection<Value> values = testExecution.getValues();
      Collection<Value> values = null;

      if (values == null || values.isEmpty()) {
         return;
      }

      Map<Metric, Double> results = new HashMap<>();
      for (Value value : values) {
         results.put(value.getMetric(), value.getResultValue());
      }

      List<Alert> failedAlerts = new ArrayList<>();
      Map<Alert, Map<String, Object>> failedAlertsVariables = new HashMap<>();
      for (Metric metric : results.keySet()) {
         List<Alert> alerts = alertDAO.getByTestAndMetric(test, metric);
         for (Alert alert : alerts) {
            if (!hasAlertAllTags(testExecution, alert)) {
               continue;
            }

            if (!conditionChecker.checkCondition(alert.getCondition(), testExecution, metric)) {
               failedAlerts.add(alert);
               failedAlertsVariables.put(alert, conditionChecker.getEvaluatedVariables());
            }
         }
      }

      alertingReporterService.setConditionVariables(failedAlertsVariables);
      alertingReporterService.reportAlert(failedAlerts, testExecution);
   }

   @Override
   public void checkConditionSyntax(String condition, Metric metric) {
       conditionChecker.checkConditionSyntax(condition, metric);
   }

   /**
    * Helper method. Retrieves all the associated entities, because we need them in managed state.
    *
    * @param alert
    */
   private void makeManaged(Alert alert) {
      Test test = testDAO.get(alert.getTest().getId());
      alert.setTest(test);

      Metric metric = metricDAO.get(alert.getMetric().getId());
      alert.setMetric(metric);

      Set<Tag> tags = new HashSet<>();
      if (alert.getTags() != null) {
         for (Tag tag : alert.getTags()) {
            Tag managedTag = tagDAO.findByName(tag.getName());
            tags.add(managedTag);
         }

         alert.setTags(tags);
      }
   }

   /**
    * Helper method. We can specify tags that filter alerts that should be present on the test execution. This method
    * return true/false if all tags required by alert, are present on test execution.
    *
    * @param testExecution
    * @param alert
    * @return
    */
   private boolean hasAlertAllTags(TestExecution testExecution, Alert alert) {
      for (Tag tag : alert.getTags()) {
         if (!testExecution.getTags().contains(tag.getName())) {
            return false;
         }
      }

      return true;
   }

}
