/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.perfrepo.client.PerfRepoClient;
import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.ValueParameter;
import org.perfrepo.model.report.Report;
import org.perfrepo.model.report.ReportProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		assertEquals(test2.getTestExecutions(), null);

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
		assertEquals(test2.getTestExecutions(), null);

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
		assertEquals(testExecution2.getAttachments().size(), 0);
		assertEquals(testExecution2.getParameters().size(), 2);

		Map<String, String> params = testExecution2.getParametersAsMap();
		assertEquals(params.get("param1"), "value1");
		assertEquals(params.get("param2"), "value2");
		assertEquals(testExecution2.getTestExecutionTags().size(), 2);

		List<String> tags = testExecution2.getSortedTags();
		assertEquals(tags.size(), 2);
		assertEquals(tags.get(0), "tag1");
		assertEquals(tags.get(1), "tag2");
		assertEquals(testExecution2.getValues().size(), 2);
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
		long salt = System.currentTimeMillis();
		return Test.builder()
				.name("test1" + salt)
				.groupId(clientGroup)
				.uid("testtestuid" + salt)
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

	private TestExecution createTestExecution(Long testId) {
		return TestExecution.builder()
				.testId(testId)
				.name("execution1")
				.started(new Date())
				.parameter("param1", "value1")
				.parameter("param2", "value2")
				.tag("tag1")
				.tag("tag2")
				.value("metric1", 12.0d)
				.value("metric2", 8.0d)
				.build();
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
		assertEquals(actual.getTestMetrics(), expected.getTestMetrics());
		assertEquals(actual.getValues(), expected.getValues());
	}

	private static void assertMetricListEquals(Collection<Metric> actual, Collection<Metric> expected) {
		assertEquals(actual == null, expected == null);
		if (actual == null) {
			return;
		}

		assertEquals(expected.size(), actual.size());
		expected.stream().forEach(expectedMetric -> actual.stream().anyMatch(actualMetric -> expectedMetric.getName().equals(actualMetric.getName())));
	}
}
