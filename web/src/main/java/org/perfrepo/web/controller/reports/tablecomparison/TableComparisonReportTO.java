package org.perfrepo.web.controller.reports.tablecomparison;

import java.util.List;

public class TableComparisonReportTO {

   private String name;
   private String description;
   private List<Group> groups;

   public TableComparisonReportTO(String name, String reportDescription, List<Group> groups) {
      this.name = name;
      this.description = reportDescription;
      this.groups = groups;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<Group> getGroups() {
      return groups;
   }

   public void setGroups(List<Group> groups) {
      this.groups = groups;
   }
}
