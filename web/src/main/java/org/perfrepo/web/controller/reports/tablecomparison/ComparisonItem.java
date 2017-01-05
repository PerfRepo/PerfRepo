package org.perfrepo.web.controller.reports.tablecomparison;

import org.perfrepo.model.TestExecution;

public class ComparisonItem {

   private String alias;
   private boolean baseline;

   // if the Comparison.ChooseOption.EXECUTION_ID
   private Long executionId;

   // if Comparison.ChooseOption.SET_OF_TAGS
   private String testUid;
   private String tags;

   // this is a transient field, only filled in once the report is being shown (it is not saved in db)
   private TestExecution testExecution;

   public ComparisonItem() {
      alias = "New item alias";
      baseline = false;
      executionId = -1L;
      testUid = "";
      tags = "";
   }

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

   public String getTestUid() {
      return testUid;
   }

   public void setTestUid(String testUid) {
      this.testUid = testUid;
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public TestExecution getTestExecution() {
      return testExecution;
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

}
