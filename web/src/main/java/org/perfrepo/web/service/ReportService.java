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

import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.to.MetricReportTO;
import org.perfrepo.web.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.List;

/**
 * Service layer for all operations related to reports.
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
public interface ReportService {

   /**
    * Get all reports that associated (visible) with currently logged user.
    *
    * @return List of {@link Report}
    */
   public List<Report> getAllUsersReports();

   /**
    * Get all reports for which exists any (READ, WRITE or PUBLIC) permission to logged user or user group.
    * @return List of {@link Report}
    */
   public List<Report> getAllReports();

   /**
    * Get all reports for which exists WRITE permission to any user group.
    * @return List of {@link Report}
    */
   public List<Report> getAllGroupReports();

   /**
    * Removes report
    *
    * @param report
    * @throws org.perfrepo.web.service.exceptions.ServiceException
    */
   public void removeReport(Report report);

   /**
    * Create new report
    *
    * @param report
    * @return created {@link Report}
    */
   public Report createReport(Report report);

   /**
    * Update existing report
    *
    * @param report
    * @return updated {@link Report}
    */
   public Report updateReport(Report report);

   /**
    * Computes metric report.
    *
    * @param request
    * @return response TO
    */
   public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request);

   /**
    * Get report with all properties
    *
    * @param report
    * @return {@link Report} with all attributes fetched
    */
   public Report getFullReport(Report report);


   /**
    * Returns all permission to report. If the permissions are not assigned, returns default permission.
    *
    * @param report
    * @return
    */
   public Collection<Permission> getReportPermissions(Report report);

    /**
     * Adds permission to provided report.
     *
     * @param permission
     */
   public void addPermission(Permission permission) throws ServiceException;

   /**
    * Updates permission to provided report.
    *
    * @param permission
    */
   public void updatePermission(Permission permission) throws ServiceException;

   /**
    * Deletes permission from provided report.
    *
    * @param permission
    */
   public void deletePermission(Permission permission) throws ServiceException;
}
