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
package org.perfrepo.web.controller.reports.testgroup;

import com.google.common.collect.*;
import org.perfrepo.model.Tag;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.auth.AccessType;
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;
import org.perfrepo.model.user.User;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.controller.reports.ReportPermissionController;
import org.perfrepo.web.security.AuthorizationService;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.session.TEComparatorSession;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.util.ReportUtils;
import org.perfrepo.web.util.ValueComparator;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class TestGroupReportController extends BaseController {

   private static final String CHART_COLOR_RED = "#BD4247";

   private static final String CHART_COLOR_ORANGE = "#ECA778";

   private static final String CHART_COLOR_GREEN = "#1B8D1B";

   /**
    * Serial Version UID
    */
   private static final long serialVersionUID = -482624457203937471L;

   @Inject
   private TestService testService;

   @Inject
   private UserService userService;

   @Inject
   private ReportService reportService;

   @Inject
   private UserSession userSession;

   @Inject
   private ReportPermissionController reportAccessController;

   @Inject
   private TEComparatorSession teComparator;

   @Inject
   private AuthorizationService authorizationService;

   /**
    * Test name, tag, value
    */
   private HashBasedTable<String, ColumnKey, ValueCell> data = HashBasedTable.create();

   /**
    * Report properties
    */
   private Long reportId = null;

   private String reportName = null;

   private boolean isCloning = false;

   private Report report;

   private boolean userAuthorized = true;

   private List<String> tags = Lists.newArrayList();

   private Map<String, String> tagAlias = new HashMap<String, String>();

   private Collection<Long> testIds = Sets.newHashSet();

   private List<String> tests = Lists.newArrayList();

   private List<String> metrics = Lists.newArrayList();

   private List<String> selectedMetrics = Lists.newArrayList();

   private Map<String, List<String>> comparison = new HashMap<String, List<String>>();

   private Double semiNegativeThreshold = -5.0;

   private Double confThreshold;

   private NumberFormat formatter = new DecimalFormat("#0.00");

   private List<String> testsCopy;

   private String currentTest;

   private List<String> tagsCopy;

   private List<String> newFoundTags = Lists.newArrayList();

   private int testExecutionsFound = 0;

   private Map<String, String> tagAliasCopy;

   private Map<String, List<String>> comparisonCopy;

   private String currentTag;

   private String baseline1 = null;

   private String baseline2 = null;

   private String newReportName;

   private List<TestExecution> testExecutions = new ArrayList<TestExecution>();

   private List<TestExecution> missingTE = new ArrayList<TestExecution>();

   public void processTestExecutions() {
      data.clear();
      reloadSessionMessages();
      //get TEs
      testExecutions = findTestExecutions();
      //filter tags according to test execution result
      //tags.clear();
      for (TestExecution te : testExecutions) {
         String tagsKey = normalizeTags(te.getTags());
         if (!tags.contains(tagsKey)) {
            tags.add(tagsKey);
         }
         String columnKey1 = tagAlias.get(tagsKey) != null ? tagAlias.get(tagsKey) : tagsKey;
         //TODO: solve this
         /*
         for (Value value : te.getValues()) {
            String metricName = value.getMetricName();
            if (!metrics.contains(metricName)) {
               metrics.add(metricName);
            }
            if (selectedMetrics.isEmpty() || selectedMetrics.contains(metricName)) {
               ColumnKey columnKeyFull = new ColumnKey(columnKey1, metricName);
               ValueCell valueCell = data.get(te.getTestUid(), columnKeyFull);
               if (valueCell == null) {
                  data.put(te.getTestUid(), columnKeyFull, new ValueCell(value));
               } else {
                  valueCell.addValues(te.getValues());
               }
            }
         }*/
      }
      if (selectedMetrics.isEmpty()) {
         selectedMetrics.addAll(metrics);
      }
   }

   public List<TestExecution> findTestExecutions() {
      if (testIds.isEmpty() && !tags.isEmpty() && !tests.isEmpty()) {
         missingTE.clear(); //may be useless step
         List<TestExecution> allTestExecutions = testService.getTestExecutions(tags, tests);
         // build list of test execution ids
         for (TestExecution te : allTestExecutions) {
            if (!testIds.contains(te.getId())) {
               testIds.add(te.getId());
            }
         }
         return allTestExecutions;
      } else if (!testIds.isEmpty()) {
         //find defined test executions by id
         List<TestExecution> testExecutions = testService.getFullTestExecutions(testIds);
         boolean initial = false;
         //is tests && tags are empty - when we go to the report from the search page
         if (tests.isEmpty() && tags.isEmpty()) {
            initial = true;
         }
         List<TestExecution> filtered = new ArrayList<TestExecution>();
         for (TestExecution te : testExecutions) {
            if (initial) {
               if (!tests.contains(te.getTestUid())) {
                  tests.add(te.getTestUid());
               }
               String tagsKey = normalizeTags(te.getTags());
               if (!tags.contains(tagsKey)) {
                  tags.add(tagsKey);
               }
               filtered.add(te);
            } else {
               if (tests.contains(te.getTestUid()) && tags.contains(normalizeTags(te.getTags()))) {
                  filtered.add(te);
               } else {
                  testIds.remove(te.getId());
               }
            }
         }
         //find missing test execution that can be compared in the report - allowed only when user is authorized for write access
         if (userAuthorized) {
            List<TestExecution> allTestExecutions = testService.getTestExecutions(tags, tests);
            for (TestExecution te : allTestExecutions) {
               if (!filtered.contains(te) && !missingTE.contains(te)) {
                  missingTE.add(te);
               }
            }
         }
         return filtered;
      }
      return new ArrayList<TestExecution>();
   }

   @PostConstruct
   private void readConfiguration() {
      try {
         String reportIdParam = getRequestParam("reportId");
         reportId = reportIdParam != null ? Long.parseLong(reportIdParam) : null;
         if (reportId != null) {
            userAuthorized = authorizationService.isUserAuthorizedFor(AccessType.WRITE, new Report(reportId));
            Report report = reportService.getFullReport(new Report(reportId));
            if (report != null) {
               reportName = report.getName();
               Map<String, ReportProperty> properties = report.getProperties();
               if (properties != null && !properties.isEmpty()) {
                  //ids
                  if (properties.get("testIds") != null) {
                     String idsProperty = properties.get("testIds").getValue();
                     if (idsProperty != null) {
                        for (String item : idsProperty.split(", ")) {
                           testIds.add(Long.valueOf(item));
                        }
                     }
                  }
                  //tests
                  String testsProperty = properties.get("tests").getValue();
                  tests = new ArrayList<String>();
                  if (testsProperty != null) {
                     tests = Lists.newArrayList(testsProperty.split(", "));
                  }
                  //tags
                  int i = 1;
                  tags = new ArrayList<String>();
                  tagAlias = new HashMap<String, String>();
                  String tag = properties.containsKey("tag." + i) ? properties.get("tag." + i).getValue() : null;
                  while (tag != null) {
                     tags.add(tag);
                     String ta = properties.containsKey("tag." + i + ".alias") ? properties.get(
                         "tag." + i + ".alias").getValue() : null;
                     if (ta != null) {
                        tagAlias.put(tag, ta);
                     }
                     i++;
                     tag = properties.containsKey("tag." + i) ? properties.get("tag." + i).getValue() : null;
                  }
                  //comparison
                  i = 1;
                  comparison = new HashMap<String, List<String>>();
                  String compare = properties.containsKey("compare." + i + ".1") ? properties.get(
                      "compare." + i + ".1").getValue() : null;
                  while (compare != null) {
                     String c1 = properties.containsKey("compare." + i + ".1") ? properties.get(
                         "compare." + i + ".1").getValue() : null;
                     String c2 = properties.containsKey("compare." + i + ".2") ? properties.get(
                         "compare." + i + ".2").getValue() : null;
                     String alias = properties.containsKey("compare." + i + ".alias") ? properties.get(
                         "compare." + i + ".alias").getValue() : null;
                     comparison.put(alias, Lists.newArrayList(c1, c2));
                     i++;
                     compare = properties.containsKey("compare." + i + ".1") ? properties.get("compare." + i + ".1")
                         .getValue() : null;
                  }
                  String metricsProperty = properties.containsKey("metrics") ? properties.get("metrics").getValue()
                      : null;
                  if (metricsProperty != null) {
                     selectedMetrics.addAll(Lists.newArrayList(metricsProperty.split(", ")));
                  }
                  // threshold
                  String thresholdProperty = properties.containsKey("configuration.threshold") ? properties.get("configuration.threshold").getValue() : null;
                  if (thresholdProperty != null) {
                     semiNegativeThreshold = Double.valueOf(thresholdProperty);
                  } else {
                     semiNegativeThreshold = -5.0;
                  }
               }
            }
         } else if (teComparator.isAnyToCompare()) {
            testIds.addAll(teComparator.getExecIds());
         }
         newReportName = reportName;
         processTestExecutions();
      } catch (Exception e) {
         if (e.getCause() instanceof SecurityException) {
            //addMessage(ERROR, "page.report.permissionDenied");
            redirectWithMessage("/reports", ERROR, "page.report.permissionDenied");
         } else {
            redirectWithMessage("/reports", ERROR, "page.report.error");
         }
      }
   }

   public void saveReport() {
      saveReport(reportName);
   }

   private void saveReport(String reportName) {
      Report report = null;

      User user = userService.getUser(userSession.getUser().getId());

      if (reportId != null) {
         report = reportService.getFullReport(new Report(reportId));
      }

      if (report == null) {
         report = new Report();
      }
      report.setName(reportName);
      report.setType("TestGroupReport");
      report.setUser(user);

      Map<String, ReportProperty> properties = report.getProperties();
      if (properties == null) {
         properties = new HashMap<String, ReportProperty>();
      }

      //ids
      String idsProperty = "";
      for (Long id : testIds) {
         idsProperty += id + ", ";
      }
      if (idsProperty.length() > 0) {
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "testIds",
                                                       idsProperty.substring(0, idsProperty.length() - 2), report);
      }

      //tests
      String testsProperty = "";
      for (String test : tests) {
         testsProperty += test + ", ";
      }
      if (testsProperty.length() > 0) {
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "tests",
                                                       testsProperty.substring(0, testsProperty.length() - 2), report);
      }

      //tags
      int i = 1;
      for (String tag : tags) {
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "tag." + i, tag, report);
         if (tagAlias.get(tag) != null) {
            ReportUtils.createOrUpdateReportPropertyInMap(properties, "tag." + i + ".alias", tagAlias.get(tag),
                                                          report);
         }
         i++;
      }
      //remove all other tags
      while (properties.remove("tag." + i) != null) {
         properties.remove("tag." + i + ".alias");
         i++;
      }

      //comparison
      i = 1;
      for (String key : comparison.keySet()) {
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "compare." + i + ".1",
                                                       comparison.get(key).get(0), report);
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "compare." + i + ".2",
                                                       comparison.get(key).get(1), report);
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "compare." + i + ".alias", key, report);
         i++;
      }
      //remove all other comparisons
      while (properties.remove("compare." + i + ".1") != null) {
         properties.remove("compare." + i + ".2");
         properties.remove("compare." + i + ".alias");
         i++;
      }

      String metricsProperty = "";
      for (String metric : selectedMetrics) {
         metricsProperty += metric + ", ";
      }

      if (metricsProperty.length() > 0) {
         ReportUtils.createOrUpdateReportPropertyInMap(properties, "metrics",
                                                       metricsProperty.substring(0, metricsProperty.length() - 2), report);
      }
      ReportUtils.createOrUpdateReportPropertyInMap(properties, "configuration.threshold", String.valueOf(semiNegativeThreshold), report);
      report.setProperties(properties);
      //TODO: solve this
      //report.setPermissions(reportAccessController.getPermissionsOld());
      if (report.getId() == null) {
         reportService.createReport(report);
      } else {
         reportService.updateReport(report);
      }
      addSessionMessage(INFO, "page.reports.testGroup.reportSaved", reportName);
      reloadSessionMessages();
   }

   /**
    * Method responsible for clone existing report.
    */
   public void cloneReport() {
      reportId = null;
      //copy permissions to new report
      reportAccessController.setPermissionsOld(copyPermissions());
      saveReport(newReportName);
      redirectWithMessage("/reports", INFO, "page.reports.testGroup.reportSaved", newReportName);
   }

   /**
    * Helper method used for clone Report functionality. It copies permissions from existing report to new one.
    * @return Copied permissions
    */
   private Collection<Permission> copyPermissions() {
      Collection<Permission> permissions = reportAccessController.getPermissionsOld();
      List<Permission> clonedPermissions = new ArrayList<Permission>();
      for (Permission p : permissions) {
         Permission newPerm = p;
         newPerm.setId(null);
         newPerm.setReport(null);
         clonedPermissions.add(newPerm);
      }
      return clonedPermissions;
   }

   public void updateReportName() {
      reportName = newReportName;
   }

   public List<String> getTests() {
      List<String> result = new ArrayList<String>(tests);
      Collections.sort(result);
      return result;
   }

   public List<String> getTags() {
      return tags;
   }

   public List<String> getTableTags() {
      List<String> tags = new ArrayList<String>();
      for (ColumnKey key : data.columnKeySet()) {
         if (!tags.contains(key.getTagKey())) {
            tags.add(key.getTagKey());
         }
      }
      return tags;
   }

   public ValueCell getValue(String test, String tags, String metric) {
      return data.get(test, new ColumnKey(tags, metric));
   }

   public List<String> getComparisonValues() {
      return new ArrayList<String>(comparison.keySet());
   }

   private String normalizeTags(Collection<Tag> tagCollection) {
      List<String> tags = tagCollection.stream().map(Tag::getName).collect(Collectors.toList());
      Collections.sort(tags);
      String result = "";
      for (String tag : tags) {
         result += tag + " ";
      }
      return result.substring(0, result.length() - 1);
   }

   public List<String> parseTags(String tags) {
      if (tags != null) {
         return Lists.newArrayList(tags.split(" "));
      }
      return null;
   }

   public float compare(String test, String compareKey, String metric) {
      List<String> compareColumns = comparison.get(compareKey);
      if (compareColumns != null && compareColumns.size() == 2) {
         ColumnKey columnKey1 = new ColumnKey(comparison.get(compareKey).get(0), metric);
         ColumnKey columnKey2 = new ColumnKey(comparison.get(compareKey).get(1), metric);
         Value value1 = data.get(test, columnKey1) != null ? data.get(test, columnKey1).getBestValue() : null;
         Value value2 = data.get(test, columnKey2) != null ? data.get(test, columnKey2).getBestValue() : null;
         if (value1 != null && value2 != null) {
            return compareValues(value1, value2);
         }
         return 0f;
      }
      return 0f;
   }

   public String getStyle(float result) {
      if (result > 0) {
         return "green";
      } else if (result < semiNegativeThreshold) {
         return "red";
      } else {
         return "orange";
      }
   }

   private float compareValues(Value v1, Value v2) {
      return (float) (((v2.getResultValue() - v1.getResultValue()) * 100f) / v2.getResultValue());
   }

   public String format(Object number) {
      if (number != null) {
         return formatter.format(number);
      } else {
         return "";
      }
   }

   public String getTagAlias(String tag) {
      return tagAlias.get(tag);
   }

   public String getTestName(String testuid) {
      for (TestExecution te : getTestExecutions()) {
         if (testuid.equals(te.getTestUid())) {
            return te.getTest().getName();
         }
      }
      return null;
   }

   public Map<String, String> getTagAlias() {
      return tagAlias;
   }

   public void setTagAlias(Map<String, String> tagAlias) {
      this.tagAlias = tagAlias;
   }

   public List<String> autocompleteTests(String test) {
      List<String> tests = testService.getTestsByPrefix(test);
      java.util.Iterator<String> it = tests.iterator();
      while (it.hasNext()) {
         String t = it.next();
         if (testsCopy != null && this.testsCopy.contains(t)) {
            it.remove();
         }
      }
      return tests;
   }

   public void removeTest(String test) {
      this.testsCopy.remove(test);
   }

   public void addTest() {
      if (currentTest != null) {
         if (testService.getTestByUID(currentTest) != null) {
            this.testsCopy.add(currentTest);
         } else {
            addSessionMessage(ERROR, "page.reports.testGroup.testNotExists", currentTest);
            reloadSessionMessages();
         }
      }
      currentTest = null;
   }

   public void storeTests() {
      tests = testsCopy;
      testsCopy = null;
      processTestExecutions();
   }

   public void storeConfiguration() {
      semiNegativeThreshold = confThreshold;
      confThreshold = null;
   }

   public List<String> getTestsCopy() {
      if (testsCopy == null) {
         testsCopy = new ArrayList<String>(tests);
      }
      return testsCopy;
   }

   public void clearTestsCopy() {
      testsCopy = null;
   }

   public String getCurrentTag() {
      return currentTag;
   }

   public void setCurrentTag(String currentTag) {
      this.currentTag = currentTag;
   }

   public List<String> autocompleteTags(String tag) {
      List<String> tests = testService.getTagsByPrefix(tag);
      java.util.Iterator<String> it = tests.iterator();
      while (it.hasNext()) {
         String t = it.next();
         if (tagsCopy != null && this.tagsCopy.contains(t)) {
            it.remove();
         }
      }
      return tests;
   }

   public void removeTag(String tag) {
      this.tagsCopy.remove(tag);
   }

   public void addTag() {
      if (currentTag != null) {
         List<TestExecution> tes = testService.getTestExecutions(Lists.newArrayList(currentTag), tests);
         newFoundTags = Lists.newArrayList();
         for (TestExecution te : tes) {
            String t = normalizeTags(te.getTags());
            if (!tagsCopy.contains(t)) {
               tagsCopy.add(t);
               newFoundTags.add(t);
            }
            if (newFoundTags.contains(t)) {
               testExecutionsFound++;
            }
         }
      }
      currentTag = null;
   }

   public void storeTags() {
      tags = tagsCopy;
      tagsCopy = null;
      // The tag change should be reflected in comparison
      if (comparison != null && !comparison.isEmpty()) {
         MapDifference<String, String> diff = Maps.difference(tagAlias, tagAliasCopy);
         if (!diff.areEqual()) {
            Map<String, MapDifference.ValueDifference<String>> diffs = diff.entriesDiffering();
            for (String key : diffs.keySet()) {
               String newValue = diffs.get(key).rightValue();
               String oldValue = diffs.get(key).leftValue();
               for (String compKey : comparison.keySet()) {
                  List<String> comp = comparison.get(compKey);
                  if (oldValue.equals(comp.get(0))) {
                     comp.set(0, newValue);
                  } else if (oldValue.equals(comp.get(1))) {
                     comp.set(1, newValue);
                  }
               }
            }
         }
      }
      tagAlias = tagAliasCopy;
      tagAliasCopy = null;
      processTestExecutions();
   }

   public Map<String, String> getTagAliasCopy() {
      return tagAliasCopy;
   }

   public void setTagAliasCopy(Map<String, String> tagAliasCopy) {
      this.tagAliasCopy = tagAliasCopy;
   }

   public List<String> getTagsCopy() {
      if (tagsCopy == null) {
         tagsCopy = new ArrayList<String>(tags);
         tagAliasCopy = new HashMap<String, String>(tagAlias);
      }
      return tagsCopy;
   }

   public void clearTagsCopy() {
      tagsCopy = null;
      tagAliasCopy = null;
      newFoundTags.clear();
      testExecutionsFound = 0;
   }

   public String getCurrentTest() {
      return currentTest;
   }

   public void setCurrentTest(String currentTest) {
      this.currentTest = currentTest;
   }

   public Map<String, List<String>> getComparison() {
      return comparison;
   }

   public void setComparison(Map<String, List<String>> comparison) {
      this.comparison = comparison;
   }

   public List<String> getComparisonLabels() {
      if (comparisonCopy == null) {
         comparisonCopy = new HashMap<String, List<String>>(comparison);
      }
      return new ArrayList<String>(comparisonCopy.keySet());
   }

   public List<String> getBaselines() {
      List<String> baselines = new ArrayList<String>();
      for (String tag : tags) {
         String alias = tagAlias.get(tag);
         if (alias != null && !baselines.contains(alias)) {
            baselines.add(alias);
         } else if (alias == null && !baselines.contains(tag)) {
            baselines.add(tag);
         }
      }
      return baselines;
   }

   public List<String> getBaselines2() {
      if (baseline1 != null) {
         List<String> baselines = getBaselines();
         baselines.remove(baseline1);
         return baselines;
      }
      return new ArrayList<String>();
   }

   public void addComparison() {
      if (baseline1 != null && baseline2 != null) {
         List<String> comparison = Lists.newArrayList(baseline1, baseline2);
         if (!comparisonCopy.containsValue(comparison)) {
            comparisonCopy.put(baseline2 + " vs. " + baseline1, comparison);
         }
      }
   }

   public void addAllMissingTestExecutions() {
      if (!missingTE.isEmpty()) {
         for (TestExecution te : missingTE) {
            if (!testIds.contains(te.getId())) {
               testIds.add(te.getId());
            }
            String tag = normalizeTags(te.getTags());
            if (!tags.contains(tag)) {
               tags.add(tag);
            }
         }
         missingTE.clear();
         processTestExecutions();
      }
   }

   public void addMissingTestExecution(TestExecution te) {
      if (!testIds.contains(te.getId())) {
         testIds.add(te.getId());
         String tag = normalizeTags(te.getTags());
         if (!tags.contains(tag)) {
            tags.add(tag);
         }
         missingTE.remove(te);
         processTestExecutions();
      }
   }

   public void removeTestExecutionFromReport(TestExecution te) {
      testIds.remove(te.getId());
      processTestExecutions();
   }

   public boolean isSelectedAllTE() {
      return missingTE.isEmpty();
   }

   public List<TestExecution> getMissingTE() {
      return missingTE;
   }

   public void setMissingTE(List<TestExecution> missingTE) {
      this.missingTE = missingTE;
   }

   public Double getSemiNegativeThreshold() {
      return semiNegativeThreshold;
   }

   public void setSemiNegativeThreshold(Double semiNegativeThreshold) {
      this.semiNegativeThreshold = semiNegativeThreshold;
   }

   public Double getConfThreshold() {
      if (confThreshold == null) {
         confThreshold = semiNegativeThreshold;
      }
      return confThreshold;
   }

   public void setConfThreshold(Double confThreshold) {
      this.confThreshold = confThreshold;
   }

   public List<ChartRow> getChartData(String metric, String compareKey) {
      if (comparison != null && !comparison.isEmpty() && comparison.containsKey(compareKey)) {
         List<ChartRow> chartData = new ArrayList<ChartRow>();
         List<String> tests = Lists.newArrayList(data.rowKeySet());
         Collections.sort(tests);

         for (String test : tests) {
            ChartRow row = new ChartRow();
            row.setTest(test);
            row.setResult(Double.valueOf(compare(test, compareKey, metric)));
            row.setLabel(format(row.getResult()) + '%');
            row.setColor(getChartColor(row.getResult()));
            chartData.add(row);
         }
         return chartData;
      }
      return null;
   }

   private String getChartColor(Double result) {
      return (result < semiNegativeThreshold) ? CHART_COLOR_RED : (result < 0 ? CHART_COLOR_ORANGE : CHART_COLOR_GREEN);
   }

   public void removeComparison(String label) {
      comparisonCopy.remove(label);
   }

   public int getChartWidth() {
      //one chart in row (900px)
      return 900;
   }

   public int getChartHeight() {
      //25px per test + 100px on legend and space
      return getTests().size() * 25 + 100;
   }

   public void storeComparison() {
      comparison = comparisonCopy;
      comparisonCopy = null;
      baseline1 = null;
      baseline2 = null;
      processTestExecutions();
   }

   public void clearComparisonCopy() {
      comparisonCopy = null;
      baseline1 = null;
      baseline2 = null;
   }

   public void storeMetrics() {
      processTestExecutions();
   }

   public String getBaseline1() {
      return baseline1;
   }

   public void setBaseline1(String baseline1) {
      this.baseline1 = baseline1;
   }

   public String getBaseline2() {
      return baseline2;
   }

   public void setBaseline2(String baseline2) {
      this.baseline2 = baseline2;
   }

   public Map<String, List<String>> getComparisonCopy() {
      return comparisonCopy;
   }

   public void setComparisonCopy(Map<String, List<String>> comparisonCopy) {
      this.comparisonCopy = comparisonCopy;
   }

   public Long getReportId() {
      return reportId;
   }

   public void setReportId(Long reportId) {
      this.reportId = reportId;
   }

   public String getReportName() {
      return reportName;
   }

   public void setReportName(String reportName) {
      this.reportName = reportName;
   }

   public String getNewReportName() {
      return newReportName;
   }

   public void setNewReportName(String newReportName) {
      this.newReportName = newReportName;
   }

   public List<String> getMetrics() {
      return metrics;
   }

   public void setMetrics(List<String> metrics) {
      this.metrics = metrics;
   }

   public List<String> getSelectedMetrics() {
      return selectedMetrics;
   }

   public void setSelectedMetrics(List<String> selectedMetrics) {
      this.selectedMetrics = selectedMetrics;
   }

   public boolean isCloning() {
      return isCloning;
   }

   public void setCloning(boolean cloning) {
      isCloning = cloning;
   }

   public Report getReport() {
      return report;
   }

   public void setReport(Report report) {
      this.report = report;
   }

   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public void setTestExecutions(List<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   public boolean isUserAuthorized() {
      return userAuthorized;
   }

   public void setUserAuthorized(boolean userAuthorized) {
      this.userAuthorized = userAuthorized;
   }

   public List<String> getNewFoundTags() {
      return newFoundTags;
   }

   public void setNewFoundTags(List<String> newFoundTags) {
      this.newFoundTags = newFoundTags;
   }

   public int getTestExecutionsFound() {
      return testExecutionsFound;
   }

   public void setTestExecutionsFound(int testExecutionsFound) {
      this.testExecutionsFound = testExecutionsFound;
   }

   public class ChartRow {

      private String test;
      private String label;
      private Double result;
      private String color;

      public String getTest() {
         return test;
      }

      public void setTest(String test) {
         this.test = test;
      }

      public String getLabel() {
         return label;
      }

      public void setLabel(String label) {
         this.label = label;
      }

      public Double getResult() {
         return result;
      }

      public void setResult(Double result) {
         this.result = result;
      }

      public String getColor() {
         return color;
      }

      public void setColor(String color) {
         this.color = color;
      }
   }

   public class ValueCell implements Serializable {

      private static final long serialVersionUID = 2771413301301479495L;

      private Collection<Value> values = new ArrayList<Value>();

      private Value bestValue;

      public ValueCell(Value value) {
         addValues(Arrays.asList(value));
      }

      public ValueCell(Collection<Value> values) {
         addValues(values);
      }

      public void addValues(Collection<Value> values) {
         if (values != null) {
            this.values.addAll(values);
            for (Value value : values) {
               if (bestValue == null || ValueComparator.compare(bestValue, value) < 0) {
                  bestValue = value;
               }
            }
         }
      }

      public Collection<Value> getValues() {
         return values;
      }

      public void setValues(List<Value> values) {
         this.values = values;
      }

      public Value getBestValue() {
         return bestValue;
      }

      public void setBestValue(Value bestValue) {
         this.bestValue = bestValue;
      }
   }

   public class ColumnKey {
      private String metricName;

      private String tagKey;

      public ColumnKey(String tagKey, String metricName) {
         this.tagKey = tagKey;
         this.metricName = metricName;
      }

      public String getMetricName() {
         return metricName;
      }

      public void setMetricName(String metricName) {
         this.metricName = metricName;
      }

      public String getTagKey() {
         return tagKey;
      }

      public void setTagKey(String tagKey) {
         this.tagKey = tagKey;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result
             + ((metricName == null) ? 0 : metricName.hashCode());
         result = prime * result
             + ((tagKey == null) ? 0 : tagKey.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ColumnKey other = (ColumnKey) obj;
         if (!getOuterType().equals(other.getOuterType()))
            return false;
         if (metricName == null) {
            if (other.metricName != null)
               return false;
         } else if (!metricName.equals(other.metricName))
            return false;
         if (tagKey == null) {
            if (other.tagKey != null)
               return false;
         } else if (!tagKey.equals(other.tagKey))
            return false;
         return true;
      }

      private TestGroupReportController getOuterType() {
         return TestGroupReportController.this;
      }
   }
}
