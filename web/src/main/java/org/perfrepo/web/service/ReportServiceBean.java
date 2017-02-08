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
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.PermissionDAO;
import org.perfrepo.web.dao.ReportDAO;
import org.perfrepo.web.dao.ReportPropertyDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
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
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private TestExecutionDAO testExecutionDAO;

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
      for (Permission permission: permissions) {
         permission.setReport(createdReport);
         addPermission(permission);
      }

      return createdReport;
   }

   @Override
   public Report updateReport(Report report) {
      return reportDAO.merge(report);
   }

   @Override
   public void removeReport(Report report) {
      Report managedReport = reportDAO.get(report.getId());
      reportDAO.remove(managedReport);
   }

   @Override
   public Report getReport(Long id) {
      Report managedReport = reportDAO.get(id);
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
            if ((permission.getUser() != null && permission.getUser().equals(p.getUser())) || (permission.getUser() == null && p.getUser() == null)) {
               if ((permission.getGroup() != null && permission.getGroup().equals(p.getGroup())) || (permission.getGroup() == null && p.getGroup() == null)) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Returns the default permissions WRITE group
    *
    * @return
    */
   private Set<Permission> getDefaultPermission() {
      Set<Permission> defaultPermissions = new HashSet<>();
      Permission write = new Permission();
      write.setAccessType(AccessType.WRITE);
      write.setLevel(AccessLevel.GROUP);
      User user = userSession.getLoggedUser();
      Set<Group> userGroups = userService.getUserGroups(user);
//      if (userGroups != null && userGroups.size() > 0) {
//         write.setGroup(userGroups.iterator().next().getId());
//      } else {
//         throw new IllegalStateException("User is not assigned in any group");
//      }
      defaultPermissions.add(write);
      return defaultPermissions;
   }
}
