package org.perfrepo.test;

import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.perfrepo.model.*;
import org.perfrepo.model.to.MultiValueResultWrapper;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.SingleValueResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.dao.*;
import org.perfrepo.web.util.TagUtils;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link org.perfrepo.web.dao.TestExecutionDAO}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class TestExecutionDAOTest {

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private TagDAO tagDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private ValueDAO valueDAO;

   @Inject
   private ValueParameterDAO valueParameterDAO;

   @Inject
   private TestExecutionParameterDAO testExecutionParameterDAO;

   @Inject
   private UserTransaction userTransaction;

   private Test[] tests;
   private Value[] values;
   private TestExecution[] testExecutions;
   private Metric[] metrics;

   private Calendar calendar = Calendar.getInstance();

   @Deployment
   public static Archive<?> createDeployment() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.addPackages(true, DAO.class.getPackage());
      war.addPackages(true, Entity.class.getPackage());
      war.addPackages(true, DefaultArtifactVersion.class.getPackage());
      war.addPackages(true, ArtifactResolutionException.class.getPackage());
      war.addClass(TagUtils.class);
      war.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
      war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
      return war;
   }

   @Before
   public void init() throws Exception {
      userTransaction.begin();

      createTestsAndMetrics();

      testExecutions = new TestExecution[]{testExecutionDAO.create(createTestExecution("test execution 1", createStartDate(-8), tests[0])),
                                           testExecutionDAO.create(createTestExecution("test execution 2", createStartDate(-6), tests[0])),
                                           testExecutionDAO.create(createTestExecution("test execution 3", createStartDate(-4), tests[0])),
                                           testExecutionDAO.create(createTestExecution("test execution 4", createStartDate(-2), tests[0])),
                                           testExecutionDAO.create(createTestExecution("test execution 5", createStartDate(-1), tests[1])),
                                           testExecutionDAO.create(createTestExecution("test execution 6", createStartDate(11), tests[1])),};

      values = new Value[]{createValue(10d, testExecutions[0], metrics[0]),
                           createValue(20d, testExecutions[1], metrics[0]),
                           createValue(30d, testExecutions[2], metrics[0]),
                           createValue(40d, testExecutions[3], metrics[0]),
                           createValue(1000d, testExecutions[4], metrics[0]),
                           createMultiValue(2000d, testExecutions[5], metrics[1], "Iteration", "1", "Client load", "10"),
                           createMultiValue(3000d, testExecutions[5], metrics[1], "Iteration", "2", "Client load", "20")};

      createTestExecutionParameter("param", "3", testExecutions[0]);
      createTestExecutionParameter("param", "1", testExecutions[1]);
      createTestExecutionParameter("param", "4", testExecutions[2]);
      createTestExecutionParameter("param", "2", testExecutions[3]);


      createTag("tag1", testExecutions[0]);
      createTag("tag2", testExecutions[0]);
      createTag("tag3", testExecutions[0]);
      createTag("tag1", testExecutions[1]);
      createTag("tag2", testExecutions[1]);
      createTag("tag1", testExecutions[2]);
      createTag("tag3", testExecutions[2]);
      createTag("tag4", testExecutions[3]);

      userTransaction.commit();
      userTransaction.begin();
   }

   @After
   public void cleanUp() throws Exception {
      if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
         userTransaction.commit();
      } else {
         userTransaction.rollback();
      }

      userTransaction.begin();

      tagDAO.getAll().forEach(tagDAO::remove);
      valueParameterDAO.getAll().forEach(valueParameterDAO::remove);
      valueDAO.getAll().forEach(valueDAO::remove);
      metricDAO.getAll().forEach(metricDAO::remove);
      testExecutionParameterDAO.getAll().forEach(testExecutionParameterDAO::remove);
      testExecutionDAO.getAll().forEach(testExecutionDAO::remove);
      testDAO.getAll().forEach(testDAO::remove);

      userTransaction.commit();
   }

   @org.junit.Test
   public void testGetAllByPropertyIn() {
      Collection<Object> ids = new ArrayList<>();
      ids.add(testExecutions[0].getId());
      ids.add(testExecutions[3].getId());

      List<TestExecution> result = testExecutionDAO.getAllByPropertyIn("id", ids);
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[3].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testGetAllByPropertyIdBetween() {
      Comparable from = testExecutions[1].getId();
      Comparable to = testExecutions[3].getId();
      List<TestExecution> result = testExecutionDAO.getAllByPropertyBetween("id", from, to);
      assertEquals(3, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(),
                                                   testExecutions[2].getId(),
                                                   testExecutions[3].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testGetAllByPropertyDateBetween() {
      Comparable from = createStartDate(-7);
      Comparable to = createStartDate(-3);
      List<TestExecution> result = testExecutionDAO.getAllByPropertyBetween("started", from, to);
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[2].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testGetTestExecutionsByTags() {
      List<String> tags = new ArrayList<>();
      tags.add("tag1");
      tags.add("tag2");
      List<String> testUid = new ArrayList<>();
      testUid.add(tests[0].getUid());

      List<TestExecution> result = testExecutionDAO.getTestExecutions(tags, testUid);
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testGetTestExecutionsByTagsWithLast1() {
      List<String> tags = new ArrayList<>();
      tags.add("tag1");
      List<String> testUid = new ArrayList<>();
      testUid.add(tests[0].getUid());

      List<TestExecution> result = testExecutionDAO.getTestExecutions(tags, testUid, 3, 2);
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testGetTestExecutionsByTagsWithLast2() {
      List<String> tags = new ArrayList<>();
      tags.add("tag1");
      List<String> testUid = new ArrayList<>();
      testUid.add(tests[0].getUid());

      List<TestExecution> result = testExecutionDAO.getTestExecutions(tags, testUid, 1, 1);
      assertEquals(1, result.size());
      assertEquals(testExecutions[2].getId(), result.get(0).getId());
   }

   @org.junit.Test
   public void testGetTestExecutionsByTagsWithLast3() {
      List<String> tags = new ArrayList<>();
      tags.add("tag1");
      tags.add("tag4");
      List<String> testUid = new ArrayList<>();
      testUid.add(tests[0].getUid());

      List<TestExecution> result = testExecutionDAO.getTestExecutions(tags, testUid, 5, 3);
      assertTrue(result.isEmpty());
   }

   @org.junit.Test
   public void testSearchByTestUID() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());

      assertEquals("Search by test UID retrieved unexpected results.", 4, testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult().size());
   }

   @org.junit.Test
   public void testSearchByDate() {
      Date from = createStartDate(-7);
      Date to = createStartDate(-3);

      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setStartedFrom(from);
      searchCriteria.setStartedTo(to);

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[2].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByTags() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTags("tag1 tag2");

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByTagsWithLimit() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTags("tag1");
      searchCriteria.setLimitFrom(1);
      searchCriteria.setLimitHowMany(2);

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[2].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchIdsInList() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      List<Long> ids = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());
      searchCriteria.setIds(ids);

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchWithDateAscendingOrdering() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setOrderBy(OrderBy.DATE_ASC);

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(4, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(),
                                                   testExecutions[1].getId(),
                                                   testExecutions[2].getId(),
                                                   testExecutions[3].getId());

      IntStream.range(0, expectedResultIds.size())
          .forEach(index -> assertEquals(expectedResultIds.get(index), result.get(index).getId()));
   }

   @org.junit.Test
   public void testSearchWithDateDescendingOrdering() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setOrderBy(OrderBy.DATE_DESC);

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(4, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[3].getId(),
                                                   testExecutions[2].getId(),
                                                   testExecutions[1].getId(),
                                                   testExecutions[0].getId());

      IntStream.range(0, expectedResultIds.size())
          .forEach(index -> assertEquals(expectedResultIds.get(index), result.get(index).getId()));
   }

   @org.junit.Test
   public void testSearchWithParameterAscendingOrdering() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setOrderBy(OrderBy.PARAMETER_ASC);
      searchCriteria.setOrderByParameter("param");

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(4, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(),
                                                   testExecutions[3].getId(),
                                                   testExecutions[0].getId(),
                                                   testExecutions[2].getId());

      IntStream.range(0, expectedResultIds.size())
          .forEach(index -> assertEquals(expectedResultIds.get(index), result.get(index).getId()));
   }

   @org.junit.Test
   public void testSearchWithParameterDescendingOrdering() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setOrderBy(OrderBy.PARAMETER_DESC);
      searchCriteria.setOrderByParameter("param");

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(4, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[2].getId(),
                                                   testExecutions[0].getId(),
                                                   testExecutions[3].getId(),
                                                   testExecutions[1].getId());
      IntStream.range(0, expectedResultIds.size())
          .forEach(index -> assertEquals(expectedResultIds.get(index), result.get(index).getId()));
   }

   @org.junit.Test
   public void testSearchByVersionAscendingOrdering() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setOrderBy(OrderBy.PARAMETER_ASC);
      searchCriteria.setOrderByParameter("param");

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(4, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(),
                                                   testExecutions[3].getId(),
                                                   testExecutions[0].getId(),
                                                   testExecutions[2].getId());

      IntStream.range(0, expectedResultIds.size())
          .forEach(index -> assertEquals(expectedResultIds.get(index), result.get(index).getId()));
   }

   @org.junit.Test
   public void testSearchByVersionDescendingOrdering() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setOrderBy(OrderBy.PARAMETER_DESC);
      searchCriteria.setOrderByParameter("param");

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria, Arrays.asList(tests[0].getGroupId())).getResult();
      assertEquals(4, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[2].getId(),
                                                   testExecutions[0].getId(),
                                                   testExecutions[3].getId(),
                                                   testExecutions[1].getId());
      IntStream.range(0, expectedResultIds.size())
          .forEach(index -> assertEquals(expectedResultIds.get(index), result.get(index).getId()));
   }

   @org.junit.Test
   public void testSearchValuesWithEmptyCriteria() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      Metric metric = metricDAO.get(metrics[0].getId());

      List<SingleValueResultWrapper> result = testExecutionDAO.searchValues(searchCriteria, metric, Arrays.asList(tests[0].getGroupId()));
      List<Double> expectedResult = Arrays.asList(values[0].getResultValue(),
                                                  values[1].getResultValue(),
                                                  values[2].getResultValue(),
                                                  values[3].getResultValue(),
                                                  values[4].getResultValue());

      IntStream.range(0, expectedResult.size())
          .forEach(index -> assertEquals(expectedResult.get(index), result.get(index).getValue()));
   }

   @org.junit.Test
   public void testSearchValuesWithSomeCriteria() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setTestUID(tests[0].getUid());
      searchCriteria.setStartedFrom(createStartDate(-7));
      searchCriteria.setStartedTo(createStartDate(-3));

      Metric metric = metricDAO.get(metrics[0].getId());

      List<SingleValueResultWrapper> result = testExecutionDAO.searchValues(searchCriteria, metric, Arrays.asList(tests[0].getGroupId()));
      List<Double> expectedResult = Arrays.asList(values[1].getResultValue(),
                                                  values[2].getResultValue());
      IntStream.range(0, expectedResult.size())
          .forEach(index -> assertEquals(expectedResult.get(index), result.get(index).getValue()));
   }

   @org.junit.Test
   public void testSearchMultiValues() {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      Metric metric = metricDAO.get(metrics[1].getId());

      List<MultiValueResultWrapper> result = testExecutionDAO.searchMultiValues(searchCriteria, metric, Arrays.asList(tests[1].getGroupId()));
      List<Double> expectedResult = Arrays.asList(values[5].getResultValue(),
                                                  values[6].getResultValue());

      assertTrue(expectedResult.stream().
          allMatch(expected -> result.get(0).getValues().get("Iteration").values()
              .stream().anyMatch(actual -> expected.equals(actual))));
   }

   /**
    * ------------ Helper methods for creation of test environment ------------
    */

   private void createTestsAndMetrics() {
      metrics = new Metric[] { metricDAO.create(createMetric("metric1")),
                               metricDAO.create(createMetric("metric2")) };

      Test test1 = createTest("testuser1", "uid1");
      test1.setMetrics(new HashSet<>(Arrays.asList(metrics[0])));
      Test test2 = createTest("testuser1", "uid2");
      test2.setMetrics(new HashSet<>(Arrays.asList(metrics[0])));

      tests = new Test[]{ testDAO.create(test1),
                          testDAO.create(test2) };
   }

   private Test createTest(String groupId, String uid) {
      return Test.builder()
          .name("test1")
          .groupId(groupId)
          .uid(uid)
          .description("this is a test test")
          .build();
   }

   private TestExecution createTestExecution(String name, Date startedDate, Test test) {
      TestExecution te = new TestExecution();
      te.setStarted(startedDate);
      te.setName(name);
      te.setTest(test);

      return te;
   }

   private Metric createMetric(String name) {
      Metric metric = new Metric();
      metric.setName(name);
      metric.setComparator(MetricComparator.HB);
      metric.setDescription("this is a test " + name);

      return metric;
   }

   private Tag createTag(String tagName, TestExecution storedTestExecution) {
      Tag storedTag = tagDAO.findByName(tagName);
      if (storedTag == null) {
         Tag tag = new Tag();
         tag.setName(tagName);
         storedTag = tagDAO.create(tag);
      }

      Set<Tag> tags = storedTestExecution.getTags();
      tags.add(storedTag);
      storedTestExecution.setTags(tags);

      return storedTag;
   }

   private Value createValue(Double resultValue, TestExecution testExecution, Metric metric) {
      Value value = new Value();
      value.setResultValue(resultValue);
      value.setMetric(metric);
      value.setTestExecution(testExecution);

      Value storedValue = valueDAO.create(value);
      testExecutionDAO.merge(testExecution);

      return storedValue;
   }

   private TestExecutionParameter createTestExecutionParameter(String key, String value, TestExecution testExecution) {
      TestExecutionParameter parameter = new TestExecutionParameter();
      parameter.setName(key);
      parameter.setValue(value);
      parameter.setTestExecution(testExecution);
      TestExecutionParameter storedParameter = testExecutionParameterDAO.create(parameter);

      Map<String, TestExecutionParameter> parameters = testExecution.getParameters();
      parameters.put(storedParameter.getName(), storedParameter);
      testExecution.setParameters(parameters);
      testExecutionDAO.merge(testExecution);

      return storedParameter;
   }

   /**
    * Creates multivalue
    *
    * @param testExecution
    * @param metric
    * @param valueParameters format: parameter name, parameter value, parameter name, parameter value ...
    * @return
    */
   private Value createMultiValue(Double value, TestExecution testExecution, Metric metric, String... valueParameters) {
      if (valueParameters.length % 2 != 0) {
         throw new IllegalArgumentException("Number of values arguments must be divisible by 2.");
      }

      Value storedValue = createValue(value, testExecution, metric);

      for (int i = 0; i < valueParameters.length; i += 2) {
         ValueParameter valueParameter = new ValueParameter();
         valueParameter.setName(valueParameters[i]);
         valueParameter.setParamValue(valueParameters[i + 1]);
         valueParameter.setValue(storedValue);

         ValueParameter storedValueParameter = valueParameterDAO.create(valueParameter);

         Map<String, ValueParameter> valueParameterCollection = storedValue.getParameters();
         valueParameterCollection.put(storedValueParameter.getName(), storedValueParameter);
         storedValue.setParameters(valueParameterCollection);
         valueDAO.merge(storedValue);
      }

      return storedValue;
   }

   private Date createStartDate(int daysToShift) {
      calendar.setTime(new Date());
      calendar.add(Calendar.DATE, daysToShift);

      return calendar.getTime();
   }
}
