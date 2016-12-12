package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.util.TestUtils;
import org.perfrepo.web.service.util.UserSessionMock;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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
public class TestExecutionServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private TestService testService;

    @Inject
    private TestExecutionService testExecutionService;

    @Inject
    private GroupService groupService;

    private List<Group> groups = new ArrayList<>();
    private User testUser;
    private User adminUser;
    private List<Test> tests = new ArrayList<>();

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment();
    }

    @Before
    public void init() throws DuplicateEntityException, UnauthorizedException {
        adminUser = createUser("admin");
        adminUser.setType(User.UserType.SUPER_ADMIN);
        UserSessionMock.setLoggedUser(adminUser); // hack, because we need some super admin to create a super admin :)
        userService.createUser(adminUser);
        UserSessionMock.setLoggedUser(adminUser);

        Group group = createGroup("test_group");
        groups.add(groupService.createGroup(group));

        Group group2 = createGroup("other_group");
        groups.add(groupService.createGroup(group2));

        User user = createUser("test_user");
        userService.createUser(user);
        testUser = user;

        groupService.addUserToGroup(user, group);
        UserSessionMock.setLoggedUser(user);

        Test test1 = new Test();
        fillTest("test1", test1);
        test1.setGroup(groups.get(0));
        tests.add(testService.createTest(test1));

        UserSessionMock.setLoggedUser(adminUser);
        Test test2 = new Test();
        fillTest("test2", test2);
        test2.setGroup(groups.get(1));
        tests.add(testService.createTest(test2));
        UserSessionMock.setLoggedUser(user);
    }

    @After
    public void cleanUp() throws UnauthorizedException {
        UserSessionMock.setLoggedUser(adminUser);
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
    public void testTestExecutionBasicCRUDOperations() throws DuplicateEntityException, UnauthorizedException {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);

        assertNotNull(createdTestExecution);
        assertTestExecution(testExecution, createdTestExecution);

        // test update
        TestExecution testExecutionToUpdate = createdTestExecution;
        fillTestExecution("updated_exec", tests.get(0), testExecutionToUpdate);

        TestExecution updatedTestExecution = testExecutionService.updateTestExecution(testExecutionToUpdate);
        assertTestExecution(testExecutionToUpdate, updatedTestExecution);

        // test delete
        TestExecution testExecutionToDelete = updatedTestExecution;
        testExecutionService.removeTestExecution(testExecutionToDelete);
        assertNull(testExecutionService.getTestExecution(testExecutionToDelete.getId()));
    }

    /*** HELPER METHODS ***/

    private void assertTestExecution(TestExecution expected, TestExecution actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStarted(), actual.getStarted());
        assertEquals(expected.getComment(), actual.getComment());
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

    private void fillTestExecution(String prefix, Test test, TestExecution testExecution) {
        testExecution.setName(prefix + "_name");
        testExecution.setComment(prefix + "_comment");
        testExecution.setTest(test);
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
