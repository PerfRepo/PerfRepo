/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.perfrepo.model.Alert;
import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.AlertingService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.SearchCriteriaSession;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.util.MessageUtils;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Backing bean for editing and displaying details of {@link Test}.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@ViewScoped
public class TestController extends BaseController {

   private static final long serialVersionUID = 370202307562230671L;
   private static final Logger log = Logger.getLogger(TestController.class);

   private boolean editMode;
   private boolean createMode;
   private Long testId;
   private Long alertId;

   @Inject
   private TestService testService;

   @Inject
   private UserService userService;

   @Inject
   private UserSession userSession;

   @Inject
   private SearchCriteriaSession criteriaSession;

   @Inject
   private AlertingService alertingService;

   private Test test = null;
   private Alert alertDetail = null; //field for showing detail of alert on special page
   private MetricDetails metricDetails = new MetricDetails();
   private AlertDetails alertUpdateDetails = new AlertDetails(); //field for creation/update of alert

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
      if (testId == null) {
         if (!createMode) {
            log.error("No test ID supplied");
            redirectWithMessage("/", ERROR, "page.test.errorNoTestId");
         } else {
            if (test == null) {
               test = new Test();
            }
         }
      } else {
         if (test == null) {
            test = testService.getTest(testId);
            if (test == null) {
               log.error("Can't find test with id " + testId);
               redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testId);
            } else {
               metricDetails.selectionAssignedMetrics = testService.getAvailableMetrics(test);
            }
         }
      }
   }

   public String create() {
      if (test == null) {
         throw new IllegalStateException("test is null");
      }
      try {
         Test createdTest = testService.createTest(test);
         redirectWithMessage("/test/" + createdTest.getId(), INFO, "page.test.createdSuccessfully", createdTest.getId());
      } catch (ServiceException e) {
         addMessage(e);
      } catch (org.perfrepo.web.security.SecurityException e) {
         addMessage(e);
      } catch (EJBException e) {
         if (e.getCause() != null && e.getCause().getClass() == org.perfrepo.web.security.SecurityException.class) {
            addMessage((org.perfrepo.web.security.SecurityException) e.getCause());
         } else {
            throw e;
         }
      }
      return null;
   }

   public String update() {
      if (test == null) {
         throw new IllegalStateException("test is null");
      }
      testService.updateTest(test);
      redirectWithMessage("/test/" + testId, INFO, "page.test.updatedSuccessfully");
      return null;
   }

   public void listTestExecutions() {
      //clear criterias
      TestExecutionSearchTO criteriaSession = this.criteriaSession.getExecutionSearchCriteria();
      criteriaSession.setStartedFrom(null);
      criteriaSession.setStartedTo(null);
      criteriaSession.setTags(null);
      criteriaSession.setTestName(null);

      criteriaSession.setTestUID(test.getUid());
      redirect("/exec/search");
   }

   public List<Metric> getMetricsList() {
      List<Metric> metricList = new ArrayList<Metric>();
      if (test != null) {
         metricList.addAll(test.getMetrics());
      }
      Collections.sort(metricList, ((o1, o2) -> o1.getName().compareTo(o2.getName())));
      return metricList;
   }

   public void getAlert() {
      if (alertId == null) {
         log.error("Alert ID not provided.");
         redirectWithMessage("/", ERROR, "page.alert.errorNoAlertId");
         return;
      }

      alertDetail = alertingService.getAlert(alertId);
      if (alertDetail == null) {
         log.error("Alert not found. ID: " + alertId);
         redirectWithMessage("/", ERROR, "page.alert.errorAlertNotFound", alertId);
         return;
      }
   }

   public String[] getAlertLinks() {
      if (alertDetail == null) {
         return new String[]{};
      }

      return alertDetail.getLinks().split(" ");
   }

   public List<Alert> getAlertsList() {
      Test fullTest = testService.getTest(test.getId());

      return alertingService.getAlertsList(fullTest);
   }

   public void deleteAlert(Alert alert) {
      alertingService.removeAlert(alert);
      redirectWithMessage("/test/" + testId, INFO, "page.alert.removedSuccessfully");
   }

   /** --------------- Subscription for alerting ----------------- **/

   public void addSubscriber() {
      User currentUser = userSession.getLoggedUser();
      testService.addSubscriber(currentUser, test);
      redirectWithMessage("/test/" + testId, INFO, "page.test.subscribed");
   }

   public void removeSubscriber() {
      User currentUser = userSession.getLoggedUser();
      testService.removeSubscriber(currentUser, test);
      redirectWithMessage("/test/" + testId, INFO, "page.test.unsubscribed");
   }

   public boolean isSubscribed() {
      User currentUser = userSession.getLoggedUser();
      return testService.isUserSubscribed(currentUser, test);
   }

   /** --------------- Getters/Setters ---------------- **/

   public Test getTest() {
      return test;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   public Long getTestId() {
      return testId;
   }

   public void setTestId(Long testId) {
      this.testId = testId;
   }

   public boolean isEditMode() {
      return editMode;
   }

   public void setEditMode(boolean editMode) {
      this.editMode = editMode;
   }

   public boolean isCreateMode() {
      return createMode;
   }

   public void setCreateMode(boolean createMode) {
      this.createMode = createMode;
   }

   public MetricDetails getMetricDetails() {
      return metricDetails;
   }

   public AlertDetails getAlertUpdateDetails() {
      return alertUpdateDetails;
   }

   public List<String> getUserGroups() {
      //TODO: solve this
      //return userService.getLoggedUserGroupNames();
      return null;
   }

   public Long getAlertId() {
      return alertId;
   }

   public void setAlertId(Long alertId) {
      this.alertId = alertId;
   }

   public Alert getAlertDetail() {
      return alertDetail;
   }

   public void setAlertDetail(Alert alertDetail) {
      this.alertDetail = alertDetail;
   }

   /** ----------------------------------- Helper inner classes --------------------------- **/

   /**
    * Helper class for pop-up for creating and editing metrics
    */
   public class MetricDetails {
      private boolean createMode;
      private Metric metric;
      private Long selectedAssignedMetricId;
      private List<Metric> selectionAssignedMetrics;

      public Metric getMetric() {
         return metric;
      }

      public void setMetricForUpdate(Metric metric) {
         this.metric = metric;
      }

      public void setEmptyMetric() {
         this.metric = new Metric();
      }

      public void unsetMetric() {
         this.metric = null;
      }

      public boolean isCreateMode() {
         return createMode;
      }

      public void setCreateMode(boolean createMode) {
         this.createMode = createMode;
      }

      public List<Metric> getSelectionAssignedMetrics() {
         return selectionAssignedMetrics;
      }

      public boolean isSelectionMetricVisible() {
         return selectionAssignedMetrics != null && !selectionAssignedMetrics.isEmpty();
      }

      public Long getSelectedAssignedMetricId() {
         return selectedAssignedMetricId;
      }

      public void setSelectedAssignedMetricId(Long selectedAssignedMetricId) {
         this.selectedAssignedMetricId = selectedAssignedMetricId;
      }

      public void addAssignedMetric() {
         if (selectedAssignedMetricId == null || selectionAssignedMetrics == null) {
            redirectWithMessage("/test/" + testId, ERROR, "page.test.errorNoAssignedMetric");
         } else {
            Metric selectedAssignedMetric = null;
            for (Metric m : selectionAssignedMetrics) {
               if (selectedAssignedMetricId.equals(m.getId())) {
                  selectedAssignedMetric = m;
                  break;
               }
            }
            if (selectedAssignedMetric == null) {
               redirectWithMessage("/test/" + testId, ERROR, "page.test.errorNoAssignedMetric");
               return;
            }
            try {
               testService.addMetric(selectedAssignedMetric, test);
               redirectWithMessage("/test/" + testId, INFO, "page.test.metricSuccessfullyAssigned", selectedAssignedMetric.getName());
            } catch (ServiceException e) {
               addSessionMessage(e);
            }
         }
      }

      public MetricComparator[] getMetricComparators() {
         return MetricComparator.values();
      }

      public String getEnumLabel(MetricComparator mc) {
         return MessageUtils.getEnum(mc);
      }

      public void createMetric() {
         try {
            testService.addMetric(metric, test);
            redirectWithMessage("/test/" + testId, INFO, "page.test.metricSuccessfullyCreated", metric.getName());
         } catch (ServiceException e) {
            addSessionMessage(e);
         }
      }

      public void updateMetric() {
         try {
            testService.updateMetric(metric);
         } catch (ServiceException e) {
            addSessionMessage(e);
         }
      }

      public void deleteMetric(Metric metricToDelete, Test test) {
         try {
            testService.removeMetric(metricToDelete);
            redirectWithMessage("/test/" + testId, INFO, "page.test.metricSuccessfullyDeleted", metricToDelete.getName());
         } catch (ServiceException e) {
            addSessionMessage(e);
         }
      }
   }

   /**
    * Helper class for pop-up for creating and editing alerts
    */
   public class AlertDetails {

      private Alert alert = new Alert();
      private Long metricId;
      private String tags;

      public void processAlert() {
         Metric metric = testService.getMetric(metricId);

         try {
            alertingService.checkConditionSyntax(alert.getCondition(), metric);
         } catch (RuntimeException ex) {
            addSessionMessage(ERROR, "alerting.conditionSyntax.error", ex.getMessage());
            reloadSessionMessages();
            return;
         }

         alert.setMetric(metric);
         alert.setTest(test);

         if (tags != null) {
            List<String> tagSplit = Arrays.asList(StringUtils.split(tags));
            List<Tag> tags = new ArrayList<>();
            for (String tagString : tagSplit) {
               Tag tag = new Tag();
               tag.setName(tagString);
               tags.add(tag);
            }

            //TODO: solve this
            //alert.setTags(tags);
         }

         if (alert.getId() == null) {
            alertingService.createAlert(alert);
            redirectWithMessage("/test/" + testId, INFO, "page.alert.createdSuccessfully");
         } else {
            alertingService.updateAlert(alert);
            redirectWithMessage("/test/" + testId, INFO, "page.alert.updatedSuccessfully");
         }
      }

      public void setAlert(Alert alert) {
         this.metricId = alert.getMetric().getId();

         List<String> tagsList = alert.getTags().stream().map(Tag::getName).collect(Collectors.toList());
         this.tags = StringUtils.join(tagsList, " ");

         this.alert = alert;
      }

      public Alert getAlert() {
         return alert;
      }

      public void unset() {
         alert = new Alert();
         metricId = null;
         tags = null;
      }

      public Long getMetricId() {
         return metricId;
      }

      public void setMetricId(Long metricId) {
         this.metricId = metricId;
      }

      public String getTags() {
         return tags;
      }

      public void setTags(String tags) {
         this.tags = tags;
      }
   }
}
