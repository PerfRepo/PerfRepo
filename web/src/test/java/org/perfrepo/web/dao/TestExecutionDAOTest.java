package org.perfrepo.web.dao;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.perfrepo.enums.MetricComparator;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Tag;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.TestExecutionParameter;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.ValueParameter;
import org.perfrepo.web.model.to.MultiValueResultWrapper;
import org.perfrepo.web.model.to.SingleValueResultWrapper;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;
import org.perfrepo.web.util.TestUtils;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
   private GroupDAO groupDAO;

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
      return TestUtils.createDeployment();
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
                                           testExecutionDAO.create(createTestExecution("test execution 6", createStartDate(11), tests[1])),
                                           testExecutionDAO.create(createTestExecution("test execution 7", createStartDate(13), tests[2]))};

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
      createTestExecutionParameter("param2", "55", testExecutions[3]);
      createTestExecutionParameter("labelParam", "label", testExecutions[5]);

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
   public void testSearchByTestName() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestName((tests[0].getName()));
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      assertEquals("Search by test UID retrieved unexpected results.", 4, testExecutionDAO.searchTestExecutions(searchCriteria).getResult().size());
   }

   @org.junit.Test
   public void testSearchByTestNameWithWildcard() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestName((tests[0].getName().substring(0, 2)) + "*");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup(), tests[2].getGroup())));

      assertEquals("Search by test UID retrieved unexpected results.", testExecutions.length, testExecutionDAO.searchTestExecutions(searchCriteria).getResult().size());
   }

   @org.junit.Test
   public void testSearchByTestUID() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      assertEquals("Search by test UID retrieved unexpected results.", 4, testExecutionDAO.searchTestExecutions(searchCriteria).getResult().size());
   }

   @org.junit.Test
   public void testSearchByTestUID2() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid(), tests[1].getUid()).collect(Collectors.toSet()));
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId(), testExecutions[2].getId(), testExecutions[3].getId(),
              testExecutions[4].getId(), testExecutions[5].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByDate() {
      Date from = createStartDate(-7);
      Date to = createStartDate(-3);

      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setStartedAfter(from);
      searchCriteria.setStartedBefore(to);
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[2].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByTags() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTagsQuery("tag1 tag2");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByExclusionTags() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTagsQuery("tag1 -tag2");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[2].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByComplexTagsQuery() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTagsQuery("(tag1 AND -tag3) OR tag4");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup(), tests[2].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[3].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByTagsWithLimit() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTagsQuery("tag1");
      searchCriteria.setLimitFrom(1);
      searchCriteria.setLimitHowMany(2);
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[0].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchIdsInList() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      Set<Long> ids = new HashSet<>(Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId()));
      searchCriteria.setIds(ids);
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      assertEquals(2, result.size());

      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId());

      assertTrue(expectedResultIds.stream().
          allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByGroups() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[2].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[6].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByGroups2() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[1].getGroup(), tests[2].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      assertEquals(testExecutions.length, result.size());
   }

   @org.junit.Test
   public void testSearchByParameters() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setParametersQuery("\"param\":\"2\"");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[3].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByParametersWithWildcard() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setParametersQuery("\"param\":\"*\"");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[0].getId(), testExecutions[1].getId(), testExecutions[2].getId(), testExecutions[3].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchByComplexParametersQuery() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setParametersQuery("(\"param\":\"2\" AND \"param2\":\"5%\") OR \"param\":\"1\"");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
      List<Long> expectedResultIds = Arrays.asList(testExecutions[1].getId(), testExecutions[3].getId());

      assertEquals(expectedResultIds.size(), result.size());
      assertTrue(expectedResultIds.stream().
              allMatch(expected -> result.stream().anyMatch(actual -> expected.equals(actual.getId()))));
   }

   @org.junit.Test
   public void testSearchWithDateAscendingOrdering() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setOrderBy(OrderBy.DATE_ASC);
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setOrderBy(OrderBy.DATE_DESC);
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setOrderBy(OrderBy.PARAMETER_ASC);
      searchCriteria.setOrderByParameter("param");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setOrderBy(OrderBy.PARAMETER_DESC);
      searchCriteria.setOrderByParameter("param");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setOrderBy(OrderBy.VERSION_ASC);
      searchCriteria.setOrderByParameter("param");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setOrderBy(OrderBy.VERSION_DESC);
      searchCriteria.setOrderByParameter("param");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      List<TestExecution> result = testExecutionDAO.searchTestExecutions(searchCriteria).getResult();
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));
      Metric metric = metricDAO.get(metrics[0].getId());

      List<SingleValueResultWrapper> result = testExecutionDAO.searchValues(searchCriteria, metric);
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
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setTestUIDs(Stream.of(tests[0].getUid()).collect(Collectors.toSet()));
      searchCriteria.setStartedAfter(createStartDate(-7));
      searchCriteria.setStartedBefore(createStartDate(-3));
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[0].getGroup())));

      Metric metric = metricDAO.get(metrics[0].getId());

      List<SingleValueResultWrapper> result = testExecutionDAO.searchValues(searchCriteria, metric);
      List<Double> expectedResult = Arrays.asList(values[1].getResultValue(),
                                                  values[2].getResultValue());
      IntStream.range(0, expectedResult.size())
          .forEach(index -> assertEquals(expectedResult.get(index), result.get(index).getValue()));
   }

   @org.junit.Test
   public void testSearchMultiValues() {
      TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
      searchCriteria.setLabelParameter("labelParam");
      searchCriteria.setGroups(new HashSet<>(Arrays.asList(tests[1].getGroup())));
      Metric metric = metricDAO.get(metrics[1].getId());

      List<MultiValueResultWrapper> result = testExecutionDAO.searchMultiValues(searchCriteria, metric);
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
      Test test3 = createTest("testuser2", "uid3");
      test3.setMetrics(new HashSet<>(Arrays.asList(metrics[0])));

      tests = new Test[]{ testDAO.create(test1),
                          testDAO.create(test2),
                          testDAO.create(test3) };
   }

   private Test createTest(String groupId, String uid) {
      Group group = groupDAO.findByName(groupId);
      if (group == null) {
         Group newGroup = new Group();
         newGroup.setName(groupId);
         group = groupDAO.create(newGroup);
      }

      Test test = new Test();
      test.setName("test" + uid);
      test.setUid(uid);
      test.setDescription("This is a test test");
      test.setGroup(group);

      return test;
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
      metric.setComparator(MetricComparator.HIGHER_BETTER);
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
