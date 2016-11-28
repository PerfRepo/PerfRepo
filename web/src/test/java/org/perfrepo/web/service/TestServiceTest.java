package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.OrderBy;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.search.TestSearchCriteria;
import org.perfrepo.web.service.util.TestUtils;
import org.perfrepo.web.service.util.UserSessionMock;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class TestServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private TestService testService;

    @Inject
    private GroupService groupService;

    @Inject
    private MetricDAO metricDAO;

    private Group testGroup;
    private User testUser;

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment();
    }

    @Before
    public void init() throws DuplicateEntityException {
        Group group = createGroup("test_group");
        groupService.createGroup(group);
        testGroup = group;

        User user = createUser("test");
        userService.createUser(user);
        testUser = user;

        groupService.addUserToGroup(user, group);
        UserSessionMock.setLoggedUser(user);
    }

    @After
    public void cleanUp() {
        for (User user: userService.getAllUsers()) {
            userService.removeUser(user);
        }

        for (Group group: groupService.getAllGroups()) {
            groupService.removeGroup(group);
        }

        //removing of the test also removes test executions and metrics
        for (Test test: testService.getAllTests()) {
            testService.removeTest(test);
        }
    }

    @org.junit.Test
    public void testTestCRUDOperations() throws DuplicateEntityException, UnauthorizedException {
        Test test = new Test();
        fillTest("test1", test);
        test.setGroup(testGroup);
        testService.createTest(test);

        Test createdTest = testService.getTest(test.getId());
        Test createdTestByUid = testService.getTest(test.getUid());

        assertNotNull(createdTest);
        assertNotNull(createdTestByUid);
        assertTest(test, createdTest);
        assertTest(test, createdTestByUid);

        Test duplicateTest = new Test();
        fillTest("test1", duplicateTest);
        duplicateTest.setGroup(testGroup);

        try {
            testService.createTest(duplicateTest);
            fail("TestService.createTest should fail when creating test with duplicate UID.");
        } catch (DuplicateEntityException ex) {
            // expected
        }

        // test update
        Test testToUpdate = createdTest;
        fillTest("updated_test", testToUpdate);

        Test updatedTest = testService.updateTest(testToUpdate);
        assertTest(testToUpdate, updatedTest);

        // test delete
        Test testToDelete = updatedTest;
        testService.removeTest(testToDelete);
        assertNull(testService.getTest(testToDelete.getId()));
    }

    @org.junit.Test
    public void testTestCreationToInvalidGroup() throws DuplicateEntityException, UnauthorizedException {
        Test test = new Test();
        fillTest("test1", test);
        test.setGroup(testGroup);

        // create user that is not within correct group
        User correctLoggedUser = UserSessionMock.getLoggedUserStatic();
        User user = createUser("test2");
        userService.createUser(user);
        UserSessionMock.setLoggedUser(user);

        try {
            testService.createTest(test);
            fail("TestService.createTest should fail when trying to create a test with group that user doesn't belong to.");
        } catch (UnauthorizedException ex) {
            // expected
        } finally {
            UserSessionMock.setLoggedUser(correctLoggedUser);
        }
    }

    @org.junit.Test
    public void testCreateWithMetrics() throws DuplicateEntityException, UnauthorizedException {
        Test test = new Test();
        fillTest("test", test);
        test.setGroup(testGroup);

        Metric metric1 = new Metric();
        fillMetric("metric1", metric1);
        Metric metric2 = new Metric();
        fillMetric("metric2", metric2);

        test.getMetrics().add(metric1);
        test.getMetrics().add(metric2);

        testService.createTest(test);
        Test createdTest = testService.getTest(test.getId());

        Set<String> expectedResult = new HashSet<>(Arrays.asList(metric1.getName(), metric2.getName()));
        Set<Metric> actualResult = testService.getMetricsForTest(createdTest);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.equals(actual.getName()))));
    }

    @org.junit.Test
    public void testGetAllTests() throws DuplicateEntityException, UnauthorizedException {
        Test test1 = new Test();
        fillTest("test1", test1);
        test1.setGroup(testGroup);
        testService.createTest(test1);

        Test test2 = new Test();
        fillTest("test2", test2);
        test2.setGroup(testGroup);
        testService.createTest(test2);

        Test test3 = new Test();
        fillTest("test3", test3);
        test3.setGroup(testGroup);
        testService.createTest(test3);

        List<Test> expectedResult = Arrays.asList(test1, test2, test3);
        List<Test> actualResult = testService.getAllTests();

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    @org.junit.Test
    public void testMetricCRUDOperations() throws DuplicateEntityException, UnauthorizedException {
        Test test = new Test();
        fillTest("test1", test);
        test.setGroup(testGroup);
        testService.createTest(test);

        Metric metric = new Metric();
        fillMetric("metric", metric);

        Metric createdMetric = testService.addMetric(metric, test);
        Metric retrievedMetric = testService.getMetric(createdMetric.getId());

        assertNotNull(createdMetric);
        assertNotNull(retrievedMetric);
        assertMetric(retrievedMetric, createdMetric);

        // test update
        Metric metricToUpdate = createdMetric;
        fillMetric("updated_metric", metricToUpdate);
        testService.updateMetric(metricToUpdate);

        Metric updatedMetric = testService.getMetric(metricToUpdate.getId());
        assertMetric(metricToUpdate, updatedMetric);

        // test disassociation from test
        Metric metricToDisassociate = updatedMetric;
        testService.removeMetricFromTest(metricToDisassociate, test);

        assertNull(testService.getMetric(metricToDisassociate.getId()));
        Set<Metric> testMetrics = testService.getMetricsForTest(test);
        assertTrue(!testMetrics.contains(metricToDisassociate));
    }

    @org.junit.Test
    public void testGetTestsByUidPrefix() throws DuplicateEntityException, UnauthorizedException {
        Test test1 = new Test();
        fillTest("a_test", test1);
        test1.setGroup(testGroup);
        testService.createTest(test1);

        Test test2 = new Test();
        fillTest("aa_test", test2);
        test2.setGroup(testGroup);
        testService.createTest(test2);

        Test test3 = new Test();
        fillTest("b_test", test3);
        test3.setGroup(testGroup);
        testService.createTest(test3);

        List<Test> expectedResult1 = Arrays.asList(test1, test2);
        List<Test> actualResult1 = testService.getTestsByUidPrefix("a");

        assertEquals(expectedResult1.size(), actualResult1.size());
        assertTrue(expectedResult1.stream().allMatch(expected -> actualResult1.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        List<Test> expectedResult2 = Arrays.asList(test2);
        List<Test> actualResult2 = testService.getTestsByUidPrefix("aa");

        assertEquals(expectedResult2.size(), actualResult2.size());
        assertTrue(expectedResult2.stream().allMatch(expected -> actualResult2.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    @org.junit.Test
    public void testMetricManipulationWithTest() throws DuplicateEntityException, UnauthorizedException {
        Test test1 = new Test();
        fillTest("test1", test1);
        test1.setGroup(testGroup);
        testService.createTest(test1);

        Test test2 = new Test();
        fillTest("test2", test2);
        test2.setGroup(testGroup);
        testService.createTest(test2);

        Metric metric = new Metric();
        fillMetric("metric", metric);

        Metric createdMetric = testService.addMetric(metric, test1);
        testService.addMetric(createdMetric, test2);

        Set<Metric> metricsOfTest1 = testService.getMetricsForTest(test1);
        Set<Metric> metricsOfTest2 = testService.getMetricsForTest(test2);

        Set<Metric> expectedResult = new HashSet<>(Arrays.asList(createdMetric));

        assertEquals(expectedResult.size(), metricsOfTest1.size());
        assertEquals(expectedResult.size(), metricsOfTest2.size());
        assertTrue(expectedResult.stream().allMatch(expected -> metricsOfTest1.stream()
                .anyMatch(actual -> expected.equals(actual))));
        assertTrue(expectedResult.stream().allMatch(expected -> metricsOfTest2.stream()
                .anyMatch(actual -> expected.equals(actual))));

        // test disassociation without removing the metric, because it's still associated to other test
        testService.removeMetricFromTest(createdMetric, test1);

        Set<Metric> metricsOfTest1AfterRemoval = testService.getMetricsForTest(test1);
        assertTrue(metricsOfTest1AfterRemoval.isEmpty());
        // the metric should still exist
        assertNotNull(testService.getMetric(createdMetric.getId()));

        // test disassociation of the metric, but now it should be removed completely from the database
        testService.removeMetricFromTest(createdMetric, test2);

        Set<Metric> metricsOfTest2AfterRemoval = testService.getMetricsForTest(test2);
        assertTrue(metricsOfTest2AfterRemoval.isEmpty());
        // the metric should not exist anymore
        assertNull(testService.getMetric(createdMetric.getId()));

        // test also removing metric by removing test
        Metric metric2 = new Metric();
        fillMetric("metric", metric2);

        Metric createdMetric2 = testService.addMetric(metric2, test1);
        testService.removeTest(test1);

        assertNull(testService.getMetric(createdMetric2.getId()));
    }

    @org.junit.Test
    public void testSearchTests() throws DuplicateEntityException, UnauthorizedException {
        Group group2 = createGroup("second_group");
        groupService.createGroup(group2);
        groupService.addUserToGroup(testUser, group2);

        Test test1 = new Test();
        fillTest("test1", test1);
        test1.setGroup(testGroup);
        testService.createTest(test1);

        Test test2 = new Test();
        fillTest("my_test2", test2);
        test2.setGroup(testGroup);
        testService.createTest(test2);

        Test test3 = new Test();
        fillTest("my_test3", test3);
        test3.setGroup(group2);
        testService.createTest(test3);

        // search by name without wildcard
        TestSearchCriteria searchByName = new TestSearchCriteria();
        searchByName.setName(test1.getName());
        assertSearchResultWithoutOrdering(Arrays.asList(test1), testService.searchTests(searchByName));

        // search by name with wildcard
        TestSearchCriteria searchByNameWithWildcard = new TestSearchCriteria();
        searchByNameWithWildcard.setName("my_test*");
        assertSearchResultWithoutOrdering(Arrays.asList(test2, test3), testService.searchTests(searchByNameWithWildcard));

        // search by UID
        TestSearchCriteria searchByUid = new TestSearchCriteria();
        searchByUid.setUid(test1.getUid());
        assertSearchResultWithoutOrdering(Arrays.asList(test1), testService.searchTests(searchByUid));

        // search by one group
        TestSearchCriteria searchByOneGroup = new TestSearchCriteria();
        searchByOneGroup.setGroups(new HashSet<>(Arrays.asList(testGroup)));
        assertSearchResultWithoutOrdering(Arrays.asList(test1, test2), testService.searchTests(searchByOneGroup));

        // search by more groups
        TestSearchCriteria searchByMoreGroups = new TestSearchCriteria();
        searchByMoreGroups.setGroups(new HashSet<>(Arrays.asList(testGroup, group2)));
        assertSearchResultWithoutOrdering(Arrays.asList(test1, test2, test3), testService.searchTests(searchByMoreGroups));

        // search with ordering
        TestSearchCriteria searchWithOrdering = new TestSearchCriteria();
        searchWithOrdering.setOrderBy(OrderBy.UID_DESC);
        assertSearchResultWithOrdering(Arrays.asList(test1, test3, test2), testService.searchTests(searchWithOrdering));

        // search with limit from
        TestSearchCriteria searchWithLimitFrom = new TestSearchCriteria();
        searchWithLimitFrom.setLimitFrom(1);
        searchWithLimitFrom.setOrderBy(OrderBy.NAME_ASC);
        assertSearchResultWithOrdering(Arrays.asList(test3, test1), testService.searchTests(searchWithLimitFrom), 3);

        // search with how many
        TestSearchCriteria searchWithHowMany = new TestSearchCriteria();
        searchWithHowMany.setLimitHowMany(2);
        searchWithHowMany.setOrderBy(OrderBy.NAME_DESC);
        assertSearchResultWithOrdering(Arrays.asList(test1, test3), testService.searchTests(searchWithHowMany), 3);

        // test getTestsForUser
        assertSearchResultWithoutOrdering(Arrays.asList(test1, test2, test3), testService.getTestsForUser(testUser));
    }

    @org.junit.Test
    public void testSubscribing() throws DuplicateEntityException, UnauthorizedException {
        Test test1 = new Test();
        fillTest("test1", test1);
        test1.setGroup(testGroup);
        testService.createTest(test1);

        testService.addSubscriber(testUser, test1);
        assertTrue(testService.isUserSubscribed(testUser, test1));

        testService.removeSubscriber(testUser, test1);
        assertFalse(testService.isUserSubscribed(testUser, test1));
    }

    @org.junit.Test
    public void testTestRemovalWithTestExecution() {
        //TODO: implement this to be sure that test executions are removed automatically
    }

    /*** HELPER METHODS ***/

    private void assertTest(Test expected, Test actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUid(), actual.getUid());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    private void assertMetric(Metric expected, Metric actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getComparator(), actual.getComparator());
    }

    private void fillTest(String prefix, Test test) {
        test.setName(prefix + "_name");
        test.setUid(prefix + "_uid");
        test.setDescription(prefix + "_description");
    }

    private void fillMetric(String prefix, Metric metric) {
        metric.setName(prefix + "_name");
        metric.setDescription(prefix + "_description");
        metric.setComparator(MetricComparator.HIGHER_BETTER);
    }

    private User createUser(String prefix) {
        User user = new User();
        user.setFirstName(prefix + "_first_name");
        user.setLastName(prefix + "_last_name");
        user.setEmail(prefix + "@email.com");
        user.setUsername(prefix + "_username");
        user.setPassword(prefix + "_password");

        return user;
    }

    private Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);

        return group;
    }
    
    private void assertSearchResultWithoutOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
        assertEquals(expectedResult.size(), actualResult.getTotalSearchResultsCount());
        assertEquals(expectedResult.size(), actualResult.getResult().size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.getResult().stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
        assertSearchResultWithOrdering(expectedResult, actualResult, expectedResult.size());
    }

    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult, int expectedTotalCount) {
        assertEquals(expectedTotalCount, actualResult.getTotalSearchResultsCount());
        assertEquals(expectedResult.size(), actualResult.getResult().size());

        IntStream.range(0, expectedResult.size())
                .forEach(index -> assertEquals(expectedResult.get(index), actualResult.getResult().get(index)));
    }

}
