//package org.perfrepo.web.service;
//
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.perfrepo.model.Metric;
//import org.perfrepo.model.MetricComparator;
//import org.perfrepo.model.Test;
//import org.perfrepo.model.to.SearchResultWrapper;
//import org.perfrepo.model.user.Group;
//import org.perfrepo.model.user.User;
//import org.perfrepo.web.dao.MetricDAO;
//import org.perfrepo.web.service.exceptions.DuplicateEntityException;
//import org.perfrepo.web.service.exceptions.UnauthorizedException;
//import org.perfrepo.web.service.util.TestUtils;
//import org.perfrepo.web.service.util.UserSessionMock;
//
//import javax.inject.Inject;
//import java.util.List;
//import java.util.stream.IntStream;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
///**
// * TODO: document this
// *
// * @author Jiri Holusa (jholusa@redhat.com)
// */
//@RunWith(Arquillian.class)
//public class TestExecutionServiceTest {
//
//    @Inject
//    private UserService userService;
//
//    @Inject
//    private TestService testService;
//
//    @Inject
//    private GroupService groupService;
//
//    @Inject
//    private MetricDAO metricDAO;
//
//    private Group testGroup;
//    private User testUser;
//    private Test test1;
//    private Test test2;
//
//    @Deployment
//    public static Archive<?> createDeployment() {
//        return TestUtils.createDeployment();
//    }
//
//    @Before
//    public void init() throws DuplicateEntityException, UnauthorizedException {
//        Group group = createGroup("test_group");
//        groupService.createGroup(group);
//        testGroup = group;
//
//        User user = createUser("test");
//        userService.createUser(user);
//        testUser = user;
//
//        groupService.addUserToGroup(user, group);
//        UserSessionMock.setLoggedUser(user);
//
//        Test test = new Test();
//        fillTest("test1", test);
//        test.setGroup(testGroup);
//        testService.createTest(test);
//
//    }
//
//    @After
//    public void cleanUp() {
//        for (User user: userService.getAllUsers()) {
//            userService.removeUser(user);
//        }
//
//        for (Group group: groupService.getAllGroups()) {
//            groupService.removeGroup(group);
//        }
//
//        //removing of the test also removes test executions and metrics
//        for (Test test: testService.getAllTests()) {
//            testService.removeTest(test);
//        }
//    }
//
//    @org.junit.Test
//    public void testTestExecutionCRUDOperations() throws DuplicateEntityException, UnauthorizedException {
//        Test test = new Test();
//        fillTest("test1", test);
//        test.setGroup(testGroup);
//        testService.createTest(test);
//
//        Test createdTest = testService.getTest(test.getId());
//        Test createdTestByUid = testService.getTest(test.getUid());
//
//        assertNotNull(createdTest);
//        assertNotNull(createdTestByUid);
//        assertTest(test, createdTest);
//        assertTest(test, createdTestByUid);
//
//        Test duplicateTest = new Test();
//        fillTest("test1", duplicateTest);
//        duplicateTest.setGroup(testGroup);
//
//        try {
//            testService.createTest(duplicateTest);
//            fail("TestService.createTest should fail when creating test with duplicate UID.");
//        } catch (DuplicateEntityException ex) {
//            // expected
//        }
//
//        // test update
//        Test testToUpdate = createdTest;
//        fillTest("updated_test", testToUpdate);
//
//        Test updatedTest = testService.updateTest(testToUpdate);
//        assertTest(testToUpdate, updatedTest);
//
//        // test delete
//        Test testToDelete = updatedTest;
//        testService.removeTest(testToDelete);
//        assertNull(testService.getTest(testToDelete.getId()));
//    }
//
//    /*** HELPER METHODS ***/
//
//    private void assertTest(Test expected, Test actual) {
//        assertEquals(expected.getName(), actual.getName());
//        assertEquals(expected.getUid(), actual.getUid());
//        assertEquals(expected.getDescription(), actual.getDescription());
//    }
//
//    private void assertMetric(Metric expected, Metric actual) {
//        assertEquals(expected.getName(), actual.getName());
//        assertEquals(expected.getDescription(), actual.getDescription());
//        assertEquals(expected.getComparator(), actual.getComparator());
//    }
//
//    private void fillTest(String prefix, Test test) {
//        test.setName(prefix + "_name");
//        test.setUid(prefix + "_uid");
//        test.setDescription(prefix + "_description");
//    }
//
//    private void fillMetric(String prefix, Metric metric) {
//        metric.setName(prefix + "_name");
//        metric.setDescription(prefix + "_description");
//        metric.setComparator(MetricComparator.HIGHER_BETTER);
//    }
//
//    private User createUser(String prefix) {
//        User user = new User();
//        user.setFirstName(prefix + "_first_name");
//        user.setLastName(prefix + "_last_name");
//        user.setEmail(prefix + "@email.com");
//        user.setUsername(prefix + "_username");
//        user.setPassword(prefix + "_password");
//
//        return user;
//    }
//
//    private Group createGroup(String name) {
//        Group group = new Group();
//        group.setName(name);
//
//        return group;
//    }
//
//    private void assertSearchResultWithoutOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
//        assertEquals(expectedResult.size(), actualResult.getTotalSearchResultsCount());
//        assertEquals(expectedResult.size(), actualResult.getResult().size());
//        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.getResult().stream()
//                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
//    }
//
//    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
//        assertSearchResultWithOrdering(expectedResult, actualResult, expectedResult.size());
//    }
//
//    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult, int expectedTotalCount) {
//        assertEquals(expectedTotalCount, actualResult.getTotalSearchResultsCount());
//        assertEquals(expectedResult.size(), actualResult.getResult().size());
//
//        IntStream.range(0, expectedResult.size())
//                .forEach(index -> assertEquals(expectedResult.get(index), actualResult.getResult().get(index)));
//    }
//
//}
