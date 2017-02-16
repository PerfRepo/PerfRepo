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

import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.dao.PermissionDAO;
import org.perfrepo.web.dao.ReportDAO;
import org.perfrepo.web.dao.ReportPropertyDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.model.report.Permission;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.report.ReportProperty;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.search.ReportSearchCriteria;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: document this
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServiceBean implements ReportService {

   @Inject
   private ReportDAO reportDAO;

   @Inject
   private PermissionDAO permissionDAO;

   @Inject
   private UserService userService;

   @Inject
   private GroupDAO groupDAO;

   @Inject
   private UserDAO userDAO;

   @Inject
   private ReportPropertyDAO reportPropertyDAO;

   @Inject
   private UserSession userSession;

   @Override
   public Report createReport(Report report) {
      User managedUser = userService.getUser(report.getUser().getId());
      report.setUser(managedUser);

      Report createdReport = reportDAO.create(report);

      Set<Permission> permissions = report.getPermissions();
      if (permissions.isEmpty()) {
         permissions = getDefaultPermissions();
      }

      for (Permission permission: permissions) {
         permission.setReport(createdReport);
         addPermission(permission);
      }

      saveReportProperties(report.getProperties(), createdReport);

      return createdReport;
   }

   @Override
   public Report updateReport(Report report) {
      Report managedReport = reportDAO.merge(report);
      saveReportProperties(report.getProperties(), managedReport);
      return managedReport;
   }

   @Override
   public void removeReport(Report report) {
      Report managedReport = reportDAO.get(report.getId());
      reportDAO.remove(managedReport);
   }

   @Override
   public Report getReport(Long id) {
      Report managedReport = reportDAO.get(id);
      if (managedReport == null) {
         return null;
      }

      Set<Permission> reportPermissions = getReportPermissions(managedReport);
      managedReport.setPermissions(reportPermissions);

      return managedReport;
   }

   @Override
   public List<Report> getAllReports() {
      return reportDAO.getAll();
   }

   @Override
   public List<Report> searchReports(ReportSearchCriteria criteria) {
      return null;
   }

   @Override
   public List<ReportProperty> getReportProperties(Report report) {
      return reportPropertyDAO.findByReport(report.getId());
   }

   /******** Methods related to permissions ********/

   @Override
   public void addPermission(Permission permission) {
      Report managedReport = reportDAO.get(permission.getReport().getId());
      Group managedGroup = null;
      if (permission.getGroup() != null) {
         managedGroup = groupDAO.get(permission.getGroup().getId());
      }
      User managedUser = null;
      if (permission.getUser() != null) {
         managedUser = userDAO.get(permission.getUser().getId());
      }

      permission.setReport(managedReport);
      permission.setGroup(managedGroup);
      permission.setUser(managedUser);

      permissionDAO.create(permission);
   }

   @Override
   public void updatePermission(Permission permission) {
      permissionDAO.merge(permission);
   }

   @Override
   public void deletePermission(Permission permission) {
      Permission managedPermission = permissionDAO.get(permission.getId());
      permissionDAO.remove(managedPermission);
   }

   @Override
   public Set<Permission> getReportPermissions(Report report) {
      return permissionDAO.getByReport(report.getId());
   }

    /**
     * TODO: document this
     *
     * @param newProperties
     * @param report
     */
   private void saveReportProperties(Map<String, ReportProperty> newProperties, Report report) {
      Collection<ReportProperty> oldProperties = new ArrayList<>(reportPropertyDAO.findByReport(report.getId()));
      for (ReportProperty oldProperty: oldProperties) {
         reportPropertyDAO.remove(oldProperty);
      }

      for (ReportProperty newProperty: newProperties.values()) {
         newProperty.setReport(report);
         if (newProperty.getId() == null) {
            reportPropertyDAO.create(newProperty);
         } else {
            reportPropertyDAO.merge(newProperty);
         }
      }

      report.setProperties(newProperties);
   }

   /**
    * Returns the default permissions WRITE group
    *
    * @return
    */
   private Set<Permission> getDefaultPermissions() {
      Set<Permission> defaultPermissions = new HashSet<>();

      Set<Group> userGroups = userService.getUserGroups(userSession.getLoggedUser());
      for (Group group: userGroups) {
         Permission permission = new Permission();
         permission.setAccessType(AccessType.WRITE);
         permission.setLevel(AccessLevel.GROUP);
         permission.setGroup(group);
         defaultPermissions.add(permission);
      }

      return defaultPermissions;
   }
}
