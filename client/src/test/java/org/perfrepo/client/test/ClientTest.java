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
package org.perfrepo.client.test;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import org.perfrepo.client.PerfRepoClient;

/**
 * Tests for PerfRepo REST client
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class ClientTest {

   private static final String ENCODING = "UTF-8";
   private static PerfRepoClient client;
   private static final String clientUsername = "perfrepouser";
   private static final String clientPassword = "perfrepouser1.";
   private static final String clientGroup = "perfrepouser";

   private static final String WEB_DIRECTORY = System.getProperty("web.directory") + "/web";

   /*
   TODO: this must be commented out since it will use DTOs instead
   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
      WebArchive war = ShrinkWrap.create(ZipImporter.class, "perferpo-web-test.war").importFrom(new File(WEB_DIRECTORY + "/target/perfrepo-web.war"))
          .as(WebArchive.class);

      war.delete(ArchivePaths.create("WEB-INF/classes/META-INF/persistence.xml"));
      war.delete(ArchivePaths.create("WEB-INF/jboss-web.xml"));

      war.add(new FileAsset(new File("target/test-classes/test-persistence.xml")), ArchivePaths.create("WEB-INF/classes/META-INF/persistence.xml"));
      war.add(new FileAsset(new File("target/test-classes/test-jboss-web.xml")), ArchivePaths.create("WEB-INF/jboss-web.xml"));

      return war;
   }

   @BeforeClass
   public static void createClient() {
      String host = System.getProperty("perfrepo.client.host", "localhost:8080");
      client = new PerfRepoClient(host, "testing-repo", clientUsername, clientPassword);
   }

   @AfterClass
   public static void destroyClient() {
      client.shutdown();
      client = null;
   }

   @org.junit.Test
   public void testCreateDeleteTest() throws Exception {
      Test test = createTest();
      Long id = client.createTest(test);
      assertNotNull(id);
      Test test2 = client.getTest(id);

      assertEquals(test2.getName(), test.getName());
      assertEquals(test2.getDescription(), test.getDescription());
      assertEquals(test2.getGroupId(), test.getGroupId());
      assertEquals(test2.getId(), id);
      assertEquals(test2.getUid(), test.getUid());

      client.deleteTest(id);
   }

   @org.junit.Test
   public void testGetByUid() throws Exception {
      Test test = createTest();
      Long id = client.createTest(test);
      assertNotNull(id);
      Test test2 = client.getTestByUid(test.getUid());

      assertEquals(test2.getName(), test.getName());
      assertEquals(test2.getDescription(), test.getDescription());
      assertEquals(test2.getGroupId(), test.getGroupId());
      assertEquals(test2.getId(), id);
      assertEquals(test2.getUid(), test.getUid());

      client.deleteTest(id);
   }

   @org.junit.Test
   public void testCreateDeleteTestExecution() throws Exception {
      Test test = createTest();
      Long testId = client.createTest(test);

      assertNotNull(testId);

      TestExecution testExecution = createTestExecution(testId);
      Long testExecutionId = client.createTestExecution(testExecution);

      assertNotNull(testExecutionId);

      TestExecution testExecution2 = client.getTestExecution(testExecutionId);

      assertNotNull(testExecution2);
      assertEquals(testExecution2.getId(), testExecutionId);
      assertEquals(testExecution2.getName(), testExecution.getName());
      assertEquals(testExecution2.getStarted(), testExecution.getStarted());
      assertEquals(testExecution2.getParameters().size(), 2);

      Map<String, String> params = testExecution2.getParametersAsMap();
      assertEquals(params.get("param1"), "value1");
      assertEquals(params.get("param2"), "value2");
      assertEquals(testExecution2.getTags().size(), 2);

      List<String> tags = testExecution2.getSortedTags().stream().map(Tag::getName).collect(Collectors.toList());
      assertEquals(tags.size(), 2);
      assertEquals(tags.get(0), "tag1");
      assertEquals(tags.get(1), "tag2");
      assertEquals(testExecution2.getValues().size(), 4);
      assertEquals((double) getFirstValueHavingMetricAndParameter(testExecution2, "metric1", null, null), 12.0);
      assertEquals((double) getFirstValueHavingMetricAndParameter(testExecution2, "metric2", null, null), 8.0);

      Long testExecutionId2 = client.createTestExecution(createTestExecutionWithParam(testId));
      TestExecution testExecution3 = client.getTestExecution(testExecutionId2);

      assertEquals(getFirstValueHavingMetricAndParameter(testExecution3, "multimetric", "client", "10"), 20.0d);
      assertEquals(getFirstValueHavingMetricAndParameter(testExecution3, "multimetric", "client", "20"), 40.0d);
      assertEquals(getFirstValueHavingMetricAndParameter(testExecution3, "multimetric", "client", "30"), 60.0d);

      client.deleteTestExecution(testExecutionId);
      client.deleteTestExecution(testExecutionId2);
      client.deleteTest(testId);
   }

   @org.junit.Test
   public void testCreateInvalidMultivalueTestExecution() throws Exception {
      Test test = createTest();
      Long testId = client.createTest(test);

      assertNotNull(testId);

      TestExecution testExecution = createInvalidMultivalueTestExecution(testId);
      Long testExecutionId = client.createTestExecution(testExecution);

      assertNull(testExecutionId); //this is expected, the test execution was invalid

      client.deleteTest(testId);
   }

   @org.junit.Test
   public void testUpdateTestExecution() throws Exception {
      Test test = createTest();
      Long testId = client.createTest(test);

      assertNotNull(testId);

      TestExecution testExecution = createTestExecution(testId);
      Long testExecutionId = client.createTestExecution(testExecution);
      assertNotNull(testExecutionId);

      TestExecution retrievedTestExecution = client.getTestExecution(testExecutionId);

      assertEquals(testExecution.getName(), retrievedTestExecution.getName());
      assertEquals(testExecution.getStarted(), retrievedTestExecution.getStarted());
      assertEquals(testExecution.getComment(), retrievedTestExecution.getComment());

      TestExecution updatedTestExecution = createTestExecution(testId);
      updatedTestExecution.setId(testExecutionId);
      updatedTestExecution.setName("updated test execution");
      updatedTestExecution.setComment("updated comment");

      client.updateTestExecution(updatedTestExecution);

      TestExecution retrievedUpdatedTestExecution = client.getTestExecution(testExecutionId);

      assertEquals("updated test execution", retrievedUpdatedTestExecution.getName());
      assertEquals("updated comment", retrievedUpdatedTestExecution.getComment());

      // let's try removing values, params and tags
      TestExecution reducedTestExecution = createReducedTestExecution(testId);
      reducedTestExecution.setId(retrievedTestExecution.getId());

      client.updateTestExecution(reducedTestExecution);

      TestExecution updatedReducedTestExecution = client.getTestExecution(testExecutionId);

      // values has been updated correctly
      assertEquals(reducedTestExecution.getValues().size(), updatedReducedTestExecution.getValues().size());
      assertTrue(reducedTestExecution.getValues().stream().
              allMatch(expected -> updatedReducedTestExecution.getValues().stream()
                      .anyMatch(actual -> expected.getMetricName().equals(actual.getMetricName())
                              && expected.getResultValue().equals(actual.getResultValue())
                      )
              )
      );

      // tags has been updated correctly
      assertEquals(reducedTestExecution.getTags().size(), updatedReducedTestExecution.getTags().size());
      assertTrue(reducedTestExecution.getTags().stream().
              allMatch(expected -> updatedReducedTestExecution.getTags().stream().anyMatch(actual -> expected.getName().equals(actual.getName()))));

      // parameters has been updated correctly
      assertEquals(reducedTestExecution.getParameters().size(), updatedReducedTestExecution.getParameters().size());
      assertTrue(reducedTestExecution.getParameters().stream().
              allMatch(expected -> updatedReducedTestExecution.getParameters().stream()
                      .anyMatch(actual -> expected.getName().equals(actual.getName())
                      && expected.getValue().equals(actual.getValue())
                      )
              )
      );

      client.deleteTestExecution(testExecutionId);
      client.deleteTest(testId);
   }

   @org.junit.Test
   public void testCreateDeleteAttachment() throws Exception {
      Test test = createTest();
      Long testId = client.createTest(test);

      assertNotNull(testId);

      TestExecution testExecution = createTestExecution(testId);
      Long testExecutionId = client.createTestExecution(testExecution);

      assertNotNull(testExecutionId);

      createWithContent("target/testfile.txt", "this is a test file");
      Long attachmentId = client.uploadAttachment(testExecutionId, new File("target/testfile.txt"), "text/plain", "attachment1.txt");

      assertTrue(client.downloadAttachment(attachmentId, new File("target/testfile2.txt")));

      existsWithContent("target/testfile2.txt", "this is a test file");

      client.deleteTestExecution(testExecutionId);
      client.deleteTest(testId);
   }

   @org.junit.Test
   public void testCreateReport() throws Exception {
      Report report = createReport();
      Long reportId = client.createReport(report);

      assertNotNull(reportId);
      Report retrievedReport = client.getReport(reportId);

      assertEquals(report.getName(), retrievedReport.getName());
      assertEquals(report.getType(), retrievedReport.getType());
      assertEquals(report.getProperties().size(), retrievedReport.getProperties().size());

      assertEquals(report.getProperties().get("key").getName(), retrievedReport.getProperties().get("key").getName());
      assertEquals(report.getProperties().get("key").getValue(), retrievedReport.getProperties().get("key").getValue());

      client.deleteReport(reportId);
   }

   @org.junit.Test
   public void testUpdateReport() throws Exception {
      Report report = createReport();
      Long reportId = client.createReport(report);

      assertNotNull(reportId);
      Report retrievedReport = client.getReport(reportId);

      assertEquals(report.getName(), retrievedReport.getName());
      assertEquals(report.getType(), retrievedReport.getType());
      assertEquals(report.getProperties().size(), retrievedReport.getProperties().size());

      assertEquals(report.getProperties().get("key").getName(), retrievedReport.getProperties().get("key").getName());
      assertEquals(report.getProperties().get("key").getValue(), retrievedReport.getProperties().get("key").getValue());

      Report updatedReport = createReport();
      updatedReport.setId(reportId);
      updatedReport.setName("updated report");
      updatedReport.setType("ChangedReport");

      ReportProperty newProperty = new ReportProperty();
      newProperty.setName("newKey");
      newProperty.setValue("newValue");
      newProperty.setReport(updatedReport);
      updatedReport.getProperties().put("newKey", newProperty);

      client.updateReport(updatedReport);

      Report retrievedUpdatedReport = client.getReport(reportId);

      assertEquals("updated report", retrievedUpdatedReport.getName());
      assertEquals("ChangedReport", retrievedUpdatedReport.getType());
      assertEquals(2, retrievedUpdatedReport.getProperties().size());

      assertEquals("key", retrievedUpdatedReport.getProperties().get("key").getName());
      assertEquals("value", retrievedUpdatedReport.getProperties().get("key").getValue());
      assertEquals("newKey", retrievedUpdatedReport.getProperties().get("newKey").getName());
      assertEquals("newValue", retrievedUpdatedReport.getProperties().get("newKey").getValue());

      client.deleteReport(reportId);
   }

   @org.junit.Test
   public void testReportPermissions() throws Exception {
      Report report = createReport();
      Long reportId = client.createReport(report);

      assertNotNull(reportId);
      Report retrievedReport = client.getReport(reportId);

      // default permissions are just group permission
      assertEquals(1, retrievedReport.getPermissions().size());
      Assert.assertEquals(AccessLevel.GROUP, retrievedReport.getPermissions().stream().findFirst().get().getLevel());
      Assert.assertEquals(AccessType.WRITE, retrievedReport.getPermissions().stream().findFirst().get().getAccessType());

      // add permission
      Permission newPermission = new Permission();
      newPermission.setReportId(retrievedReport.getId());
      newPermission.setLevel(AccessLevel.PUBLIC);
      newPermission.setAccessType(AccessType.READ);
      client.addReportPermission(newPermission);

      retrievedReport = client.getReport(reportId);
      assertTrue(retrievedReport.getPermissions().stream()
              .anyMatch(permission -> permission.getLevel().equals(AccessLevel.PUBLIC)
                      && permission.getAccessType().equals(AccessType.READ)));

      // delete permission
      client.deleteReportPermission(newPermission);

      retrievedReport = client.getReport(reportId);
      assertTrue(retrievedReport.getPermissions().stream().noneMatch(permission -> permission.getLevel().equals(AccessLevel.PUBLIC)));

      client.deleteReport(reportId);
   }

   @org.junit.Test
   public void testSearchTestExecutions() throws Exception {
      Test test1 = createTest("test1");
      Long test1Id = client.createTest(test1);
      Test test2 = createTest("test2");
      Long test2Id = client.createTest(test2);

      Calendar calendar = Calendar.getInstance();

      calendar.set(2016, 7, 7);
      Long testExecution1Id = client.createTestExecution(createTestExecution(test1Id, "execution1", calendar.getTime(), Arrays.asList("param1", "param2"), Arrays.asList("value1", "value2"), Arrays.asList("tag1", "tag2")));
      TestExecution testExecution1 = client.getTestExecution(testExecution1Id);
      calendar.set(2016, 7, 10);
      Long testExecution2Id = client.createTestExecution(createTestExecution(test1Id, "execution2", calendar.getTime(), Arrays.asList("param1", "param2"), Arrays.asList("value3", "value4"), Arrays.asList("tag2", "tag3")));
      TestExecution testExecution2 = client.getTestExecution(testExecution2Id);
      calendar.set(2016, 7, 13);
      Long testExecution3Id = client.createTestExecution(createTestExecution(test2Id, "execution3", calendar.getTime(), Arrays.asList("param1", "param2"), Arrays.asList("value1", "value3"), Arrays.asList("tag3", "tag4")));
      TestExecution testExecution3 = client.getTestExecution(testExecution3Id);

      List<TestExecution> result1 = client.searchTestExecutions(createSearchCriteria(Arrays.asList(testExecution1Id, testExecution2Id), null, null, null, null));
      List<TestExecution> expectedResult1 = Arrays.asList(testExecution1, testExecution2);
      assertEquals(expectedResult1.size(), result1.size());
      assertTrue(expectedResult1.stream().
              allMatch(expected -> result1.stream().anyMatch(actual -> expected.getId().equals(actual.getId()))));

      List<TestExecution> result2 = client.searchTestExecutions(createSearchCriteria(null, null, null, "tag2", null));
      List<TestExecution> expectedResult2 = Arrays.asList(testExecution1, testExecution2);
      assertEquals(expectedResult2.size(), result2.size());
      assertTrue(expectedResult2.stream().
              allMatch(expected -> result2.stream().anyMatch(actual -> expected.getId().equals(actual.getId()))));

      client.deleteTestExecution(testExecution1Id);
      client.deleteTestExecution(testExecution2Id);
      client.deleteTestExecution(testExecution3Id);
      client.deleteTest(test1Id);
      client.deleteTest(test2Id);
   }

   private TestExecutionSearchTO createSearchCriteria(List<Long> ids, Date startedFrom, Date startedTo, String tags, String testUid) {
      TestExecutionSearchTO criteria = new TestExecutionSearchTO();
      criteria.setIds(ids);
      criteria.setStartedFrom(startedFrom);
      criteria.setStartedTo(startedTo);
      criteria.setTags(tags);
      criteria.setTestUID(testUid);

      return criteria;
   }

   private Double getFirstValueHavingMetricAndParameter(TestExecution testExecution, String metric, String propName, String propValue) {
      return getValuesHavingMetricAndParameter(testExecution, metric, propName, propValue).get(0).getResultValue();
   }

   private List<Value> getValuesHavingMetricAndParameter(TestExecution testExecution, String metric, String propName, String propValue) {
      Collection<Value> values = testExecution.getValues();
      ArrayList<Value> result = new ArrayList<Value>();
      if (values == null || values.isEmpty()) {
         return result;
      } else {
         for (Value v : values) {
            if (metric.equals(v.getMetricName())) {
               if (propName == null && propValue == null) {
                  result.add(v);
               } else {
                  Collection<ValueParameter> params = v.getParameters();
                  if (params != null) {
                     for (ValueParameter p : params) {
                        if ((propName == null || propName.equals(p.getName())) && (propValue == null || propValue.equals(p.getParamValue()))) {
                           result.add(v);
                        }
                     }
                  }
               }
            }
         }
         return result;
      }
   }

   private boolean createWithContent(String file, String contentLine) throws IOException {
      File f = new File(file);
      if (f.getParentFile() != null) {
         f.getParentFile().mkdirs();
      }
      PrintWriter p = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), ENCODING));
      p.print(contentLine);
      p.close();
      return true;
   }

   private boolean existsWithContent(String file, String contentLine) throws IOException {
      BufferedReader r = null;
      try {

         File f = new File(file);
         if (!f.exists()) {
            return false;
         }
         if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
         }
         r = new BufferedReader(new InputStreamReader(new FileInputStream(f), ENCODING));
         return r.readLine().equals(contentLine);
      } finally {
         if (r != null) {
            r.close();
         }
      }
   }

   private Test createTest() {
      return createTest("test1");
   }

   private Test createTest(String name) {
      long salt = System.currentTimeMillis();
      return Test.builder()
          .name(name + salt)
          .groupId(clientGroup)
          .uid(name + "uid" + salt)
          .description("this is a test test")
          .metric("metric1", MetricComparator.LB, "this is a test metric 1")
          .metric("metric2", "this is a test metric 2")
          .metric("multimetric", MetricComparator.HB, "this is a metric with multiple values")
          .build();
   }

   private TestExecution createTestExecutionWithParam(Long testId) {
      return TestExecution.builder()
          .testId(testId)
          .name("execution1")
          .started(new Date())
          .parameter("param1", "value1")
          .parameter("param2", "value2")
          .tag("tag1")
          .tag("tag2")
          .value("multimetric", 20.0d, "client", "10")
          .value("multimetric", 40.0d, "client", "20")
          .value("multimetric", 60.0d, "client", "30").build();
   }

   private TestExecution createInvalidMultivalueTestExecution(Long testId) {
      return TestExecution.builder()
              .testId(testId)
              .name("execution1")
              .started(new Date())
              .value("multimetric", 20.0d)
              .value("multimetric", 40.0d).build();
   }

   private TestExecution createTestExecution(Long testId) {
      return createTestExecution(testId,
              "execution1",
              new Date(),
              Arrays.asList("param1", "param2"),
              Arrays.asList("value1", "value2"),
              Arrays.asList("tag1", "tag2"));
   }

   private TestExecution createTestExecution(Long testId, String name, Date started,
                                             List<String> paramNames, List<String> paramValues,
                                             List<String> tags) {
      TestExecutionBuilder builder = TestExecution.builder()
          .testId(testId)
          .name(name)
          .started(started)
          .value("metric1", 12.0d)
          .value("metric2", 8.0d)
          .value("multimetric", 20.0d, "client", "1")
          .value("multimetric", 40.0d, "client", "2");

      for (int i = 0; i < paramNames.size(); i++) {
         builder.parameter(paramNames.get(i), paramValues.get(i));
      }

      tags.stream().forEach(tag -> builder.tag(tag));

      return builder.build();
   }

   private TestExecution createReducedTestExecution(Long testId) {
      return TestExecution.builder()
              .testId(testId)
              .name("reduced execution")
              .started(new Date())
              .parameter("param1", "differentValue")
              .tag("differentTag")
              .value("metric1", 7d)
              .value("multimetric", 77d, "client", "30").build();
   }

   private Report createReport() {
      Report report = new Report();
      report.setName("report");
      report.setType("TestReport");

      ReportProperty property = new ReportProperty();
      property.setName("key");
      property.setValue("value");
      property.setReport(report);

      Map<String, ReportProperty> properties = new HashMap<>();
      properties.put("key", property);

      report.setProperties(properties);
      report.setUsername(clientUsername);

      return report;
   }

   private static void assertMetricEquals(Metric actual, Metric expected) {
      assertEquals(actual.getComparator(), expected.getComparator());
      assertEquals(actual.getDescription(), expected.getDescription());
      assertEquals(actual.getName(), expected.getName());
      assertEquals(actual.getValues(), expected.getValues());
   }

   private static void assertMetricListEquals(Collection<Metric> actual, Collection<Metric> expected) {
      assertEquals(actual == null, expected == null);
      if (actual == null) {
         return;
      }

      assertEquals(expected.size(), actual.size());
      expected.stream().forEach(expectedMetric -> actual.stream().anyMatch(actualMetric -> expectedMetric.getName().equals(actualMetric.getName())));
   }*/
}
