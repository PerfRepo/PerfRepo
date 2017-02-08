package org.perfrepo.web.service.reports.tablecomparison.model;

import org.perfrepo.web.model.TestExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jakub Markos (jmarkos@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ComparisonItem {

   private String alias;
   private boolean baseline;

   // if the Comparison.ChooseOption.EXECUTION_ID
   private Long executionId;

   // if Comparison.ChooseOption.SET_OF_TAGS
   private Long testId;
   private String tags;

   // these are transient fields, only filled in once the report is being shown (they are not saved in db)
   private List<TestExecution> testExecutions = new ArrayList<>();
   private Long comparedExecutionId; // the execution that is ultimately used for comparison

   public String getAlias() {
      return alias;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public boolean isBaseline() {
      return baseline;
   }

   public void setBaseline(boolean baseline) {
      this.baseline = baseline;
   }

   public Long getExecutionId() {
      return executionId;
   }

   public void setExecutionId(Long executionId) {
      this.executionId = executionId;
   }

   public Long getTestId() {
      return testId;
   }

   public void setTestId(Long testId) {
      this.testId = testId;
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public void setTestExecutions(List<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   public Long getComparedExecutionId() {
      return comparedExecutionId;
   }

   public void setComparedExecutionId(Long comparedExecutionId) {
      this.comparedExecutionId = comparedExecutionId;
   }
}
