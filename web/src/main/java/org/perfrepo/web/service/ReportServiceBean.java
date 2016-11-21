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
package org.perfrepo.web.service;

import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.auth.AccessLevel;
import org.perfrepo.model.auth.AccessType;
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;
import org.perfrepo.model.to.MetricReportTO;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.PermissionDAO;
import org.perfrepo.web.dao.ReportDAO;
import org.perfrepo.web.dao.ReportPropertyDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.security.Secured;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements @link{ReportService}.
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServiceBean implements ReportService {

   @Inject
   private ReportDAO reportDAO;

   @Inject
   private PermissionDAO permissionDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private UserService userService;

   @Inject
   private ReportPropertyDAO reportPropertyDAO;

   @Inject
   private UserSession userSession;

   @Override
   public List<Report> getAllUsersReports() {
      return getAllReports(userSession.getLoggedUser().getUsername());
   }

   @Override
   public List<Report> getAllReports() {
      User user = userSession.getLoggedUser();
      List<Long> groupIds = new ArrayList<Long>();
      for (Group group : user.getGroups()) {
         groupIds.add(group.getId());
      }
      return reportDAO.getByAnyPermission(user.getId(), groupIds);
   }

   @Override
   public List<Report> getAllGroupReports() {
      User user = userSession.getLoggedUser();
      List<Long> groupIds = new ArrayList<Long>();
      for (Group group : user.getGroups()) {
         groupIds.add(group.getId());
      }
      return reportDAO.getByGroupPermission(user.getId(), groupIds);
   }

   @Override
   @Secured
   public void removeReport(Report report) {
      Report r = reportDAO.get(report.getId());
      permissionDAO.removeReportPermissions(report.getId());
      reportDAO.remove(r);
   }

   @Override
   public Collection<Permission> getReportPermissions(Report report) {
      //TODO: solve this
      /*
      if (report != null && report.getId() != null) {
         return permissionDAO.getByReport(report.getId());
      } else if (report == null || report.getPermissions() == null || report.getPermissions().size() == 0) {
         return getDefaultPermission();
      } else if (report.getId() == null && report.getPermissions() != null && report.getPermissions().size() != 0) {
         return report.getPermissions();
      }*/
      return getDefaultPermission();
   }

   @Override
   public Report createReport(Report report) {
      Report r = reportDAO.create(report);
      //TODO: solve this
      //saveReportPermissions(r, report.getPermissions());
      return r;
   }

   @Override
   @Secured
   public Report updateReport(Report report) {
      //TODO: verify rights
      // somebody is able to read report
      // somebody is able to write report
      // the updater is able to write report
      //TODO: solve this
      //saveReportPermissions(report, report.getPermissions());
      updateReportProperties(report, report.getProperties());
      return reportDAO.merge(report);
   }

   @Override
   @Secured(accessType = AccessType.READ)
   public Report getFullReport(Report report) {
      Report freshReport = reportDAO.get(report.getId());
      if (freshReport == null) {
         return null;
      }

      Map<String, ReportProperty> clonedReportProperties = new HashMap<String, ReportProperty>();

      for (String propertyKey : freshReport.getProperties().keySet()) {
         clonedReportProperties.put(propertyKey, freshReport.getProperties().get(propertyKey));
      }
      List<Permission> clonedPermission = new ArrayList<Permission>();
      //TODO: solve this
      /*
      for (Permission perm : freshReport.getPermissions()) {
         clonedPermission.add(perm);
      }*/

      Report result = freshReport;
      result.setProperties(clonedReportProperties);
      return result;
   }

   @Override
   public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request) {
      MetricReportTO.Response response = new MetricReportTO.Response();
      for (MetricReportTO.ChartRequest chartRequest : request.getCharts()) {
         MetricReportTO.ChartResponse chartResponse = new MetricReportTO.ChartResponse();
         response.addChart(chartResponse);
         if (chartRequest.getTestUid() == null) {
            continue;
         } else {
            Test freshTest = testDAO.findByUid(chartRequest.getTestUid());
            if (freshTest == null) {
               // test uid supplied but doesn't exist - pick another test
               response.setSelectionTests(testDAO.getAll());
               continue;
            } else {
               freshTest = freshTest;
               chartResponse.setSelectedTest(freshTest);
               if (chartRequest.getSeries() == null || chartRequest.getSeries().isEmpty()) {
                  continue;
               }
               for (MetricReportTO.SeriesRequest seriesRequest : chartRequest.getSeries()) {
                  if (seriesRequest.getName() == null) {
                     throw new IllegalArgumentException("series has null name");
                  }
                  MetricReportTO.SeriesResponse seriesResponse = new MetricReportTO.SeriesResponse(seriesRequest.getName());
                  chartResponse.addSeries(seriesResponse);
                  if (seriesRequest.getMetricName() == null) {
                     continue;
                  }
                  Metric metric = freshTest.getMetrics().stream().filter(m -> m.getName().equals(seriesRequest.getMetricName())).findFirst().get();
                  if (metric == null) {
                     chartResponse.setSelectionMetrics(new ArrayList<>(freshTest.getMetrics()));
                     continue;
                  }
                  seriesResponse.setSelectedMetric(metric);
                  List<MetricReportTO.DataPoint> datapoints = testExecutionDAO.searchValues(freshTest.getId(), seriesRequest.getMetricName(),
                                                                                            seriesRequest.getTags(), request.getLimitSize());
                  if (datapoints.isEmpty()) {
                     continue;
                  }
                  Collections.reverse(datapoints);
                  seriesResponse.setDatapoints(datapoints);
               }

               for (MetricReportTO.BaselineRequest baselineRequest : chartRequest.getBaselines()) {
                  if (baselineRequest.getName() == null) {
                     throw new IllegalArgumentException("baseline has null name");
                  }
                  MetricReportTO.BaselineResponse baselineResponse = new MetricReportTO.BaselineResponse(baselineRequest.getName());
                  chartResponse.addBaseline(baselineResponse);
                  if (baselineRequest.getMetricName() == null) {
                     continue;
                  }
                  Metric metric = freshTest.getMetrics().stream().filter(m -> m.getName().equals(baselineRequest.getMetricName())).findFirst().get();
                  if (metric == null) {
                     chartResponse.setSelectionMetrics(new ArrayList<>(freshTest.getMetrics()));
                     continue;
                  }
                  baselineResponse.setSelectedMetric(metric);
                  baselineResponse.setExecId(baselineRequest.getExecId());
                  baselineResponse.setValue(testExecutionDAO.getValueForMetric(baselineRequest.getExecId(), baselineRequest.getMetricName()));
               }
            }
         }
      }
      return response;
   }

   @Override
   public void addPermission(Permission permission) throws ServiceException {
      if (permission.getReportId() == null) {
         throw new ServiceException("serviceException.reportIdNotSet");
      }

      Report report = reportDAO.get(permission.getReportId());
      List<Permission> oldPermissions = permissionDAO.getByReport(report.getId());
      oldPermissions.add(permission);
      saveReportPermissions(report, oldPermissions);
   }

   @Override
   public void updatePermission(Permission permission) throws ServiceException {
      if (permission.getReportId() == null) {
         throw new ServiceException("serviceException.reportIdNotSet");
      }

      Report report = reportDAO.get(permission.getReportId());
      List<Permission> oldPermissions = permissionDAO.getByReport(report.getId());
      oldPermissions.stream().filter(oldPermission -> oldPermission.getLevel().equals(permission.getLevel()))
              .forEach(oldPermission -> oldPermission.setAccessType(permission.getAccessType())
      );
      saveReportPermissions(report, oldPermissions);
   }

   @Override
   public void deletePermission(Permission permission) throws ServiceException {
      if (permission.getReportId() == null) {
         throw new ServiceException("serviceException.reportIdNotSet");
      }

      Report report = reportDAO.get(permission.getReportId());
      List<Permission> oldPermissions = permissionDAO.getByReport(report.getId());
      oldPermissions.remove(permission);
      saveReportPermissions(report, oldPermissions);
   }

   /**
    * Stores permissions to report
    *
    * @param report
    * @param newPermissions
    */
   private void saveReportPermissions(Report report, Collection<Permission> newPermissions) {
      Report freshReport = reportDAO.get(report.getId());
      List<Permission> oldPermissions = permissionDAO.getByReport(report.getId());
      // if the new permissions not defined, use default permissions
      if (newPermissions == null || newPermissions.isEmpty()) {
         newPermissions = getDefaultPermission();
      }
      for (Permission newPerm : newPermissions) {
         // if new permission has no id and the permission is not contained in the old permissions, store it
         if (newPerm.getId() == null && !isContained(newPerm, oldPermissions)) {
            newPerm.setReport(freshReport);
            permissionDAO.create(newPerm);
         }
      }
      for (Permission oldPerm : oldPermissions) {
         // if the old permission is not contained in the new collection, remove it
         if (!isContained(oldPerm, newPermissions)) {
            permissionDAO.remove(oldPerm);
         }
      }
   }

   /**
    * Helper method. Updates properties in database to match exactly the newly passed one, i.e.
    * deletes the properties that doesn't exist anymore, changes the values of the properties
    * with key that was existing and add properties with new key.
    *
    * @param report
    * @param newProperties
    */
   private void updateReportProperties(Report report, Map<String, ReportProperty> newProperties) {
      Report managedReport = reportDAO.get(report.getId());
      Map<String, ReportProperty> properties = managedReport.getProperties();

      //add newly created properties
      newProperties.keySet().stream().filter(key -> !properties.containsKey(key)).forEach(newKey -> properties.put(newKey, newProperties.get(newKey)));

      //modify existing properties
      newProperties.keySet().stream().filter(key -> properties.containsKey(key))
          .forEach(key -> properties.get(key).setValue(newProperties.get(key).getValue()));

      //delete no more existing properties
      properties.keySet().stream().filter(key -> !newProperties.containsKey(key)).forEach(key -> reportPropertyDAO.remove(properties.get(key)));
   }

   /**
    * Returns true if the permission is contained in the permission collection
    * The method checks equality of all attributes except id (accessType, level, userId, groupId, reportId).
    * The main purpose of the method is to avoid situations when the semantically same permission exists in the database and the same is created.
    *
    * @param permission
    * @param permissions
    * @return
    */
   private boolean isContained(Permission permission, Collection<Permission> permissions) {
      for (Permission p : permissions) {
         if (p.getAccessType().equals(permission.getAccessType()) && p.getLevel().equals(permission.getLevel())) {
            if ((permission.getUserId() != null && permission.getUserId().equals(p.getUserId())) || (permission.getUserId() == null && p.getUserId() == null)) {
               if ((permission.getGroupId() != null && permission.getGroupId().equals(p.getGroupId())) || (permission.getGroupId() == null && p.getGroupId() == null)) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Return all users reports
    *
    * @param username
    * @return
    */
   private List<Report> getAllReports(String username) {
      List<Report> result = new ArrayList<Report>();
      result.addAll(reportDAO.getByUser(username));
      return result;
   }

   /**
    * Returns the default permissions WRITE group
    *
    * @return
    */
   private Collection<Permission> getDefaultPermission() {
      List<Permission> defaultPermissions = new ArrayList<Permission>();
      Permission write = new Permission();
      write.setAccessType(AccessType.WRITE);
      write.setLevel(AccessLevel.GROUP);
      User user = userSession.getLoggedUser();
      if (user.getGroups() != null && user.getGroups().size() > 0) {
         write.setGroupId(user.getGroups().iterator().next().getId());
      } else {
         throw new IllegalStateException("User is not assigned in any group");
      }
      defaultPermissions.add(write);
      return defaultPermissions;
   }
}
