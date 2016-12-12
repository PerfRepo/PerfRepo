package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.Membership.MembershipType;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.IncorrectPasswordException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.util.TestUtils;
import org.perfrepo.web.service.util.UserSessionMock;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
public class UserServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private TestService testService;

    @Inject
    private GroupService groupService;

    private User adminUser;

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment();
    }

    @Before
    public void init() throws DuplicateEntityException, UnauthorizedException {
        adminUser = new User();
        adminUser.setUsername("adminUser");
        fillUser("admin", adminUser);
        adminUser.setType(User.UserType.SUPER_ADMIN);
        UserSessionMock.setLoggedUser(adminUser); // hack, because we need some super admin to create a super admin :)
        userService.createUser(adminUser);
        UserSessionMock.setLoggedUser(adminUser);
    }

    @After
    public void cleanUp() throws Exception {
        UserSessionMock.setLoggedUser(adminUser);
        // deletion of favorite parameters is done via cascade on foreign key in database
        for (User user: userService.getAllUsers()) {
            userService.removeUser(user);
        }

        for (Group group: groupService.getAllGroups()) {
            groupService.removeGroup(group);
        }

        for (Test test: testService.getAllTests()) {
            testService.removeTest(test);
        }
    }

    @org.junit.Test
    public void testUserCRUDOperations() throws Exception {
        User user = new User();
        user.setUsername("test_user");
        fillUser("test_user", user);

        userService.createUser(user);
        Long userId = user.getId();

        User createdUser = userService.getUser(userId);
        User createdUserByUsername = userService.getUser(user.getUsername());

        assertNotNull(createdUser);
        assertNotNull(createdUserByUsername);
        assertUser(user, createdUser);
        assertUser(user, createdUserByUsername);

        // test properties creation
        UserSessionMock.setLoggedUser(createdUser);

        Map<String, String> properties = new HashMap<>();
        fillProperties("version_1", "version_1", properties);
        userService.updateUserProperties(properties);

        Map<String, String> createdProperties = userService.getUserProperties(createdUser);
        assertEquals(properties, createdProperties);

        // test update
        User userToUpdate = createdUser;
        fillUser("updated_user", createdUser);

        User updatedUser = userService.updateUser(userToUpdate);
        assertUser(userToUpdate, updatedUser);

        // test update properties
        Map<String, String> propertiesToUpdate = new HashMap<>();
        fillProperties("version_1", "updated_version_1", propertiesToUpdate);
        fillProperties("version_2", "new_version_2", propertiesToUpdate);
        userService.updateUserProperties(propertiesToUpdate);

        Map<String, String> updatedProperties = userService.getUserProperties(createdUser);
        assertEquals(propertiesToUpdate, updatedProperties);

        UserSessionMock.setLoggedUser(adminUser);

        // test delete
        User userToDelete = updatedUser;
        userService.removeUser(userToDelete);
        assertNull(userService.getUser(userToDelete.getId()));
    }

    @org.junit.Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setUsername("test_user1");
        fillUser("test_user", user1);

        User user2 = new User();
        user2.setUsername("test_user2");
        fillUser("test_user2", user2);

        userService.createUser(user1);
        userService.createUser(user2);

        List<User> expectedResult = Arrays.asList(user1, user2, adminUser);
        List<User> actualResult = userService.getAllUsers();

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    @org.junit.Test
    public void testChangePassword() throws DuplicateEntityException, IncorrectPasswordException, UnauthorizedException {
        User user = new User();
        user.setUsername("test_user");
        fillUser("test_user", user);

        User createdUser = userService.createUser(user);

        UserSessionMock.setLoggedUser(createdUser);
        String newPassword = "nastyPassword9";
        userService.changePassword("test_user_password", newPassword);

        User retrievedUser = userService.getUser(user.getId());
        assertEquals(UserServiceBean.computeMd5(newPassword), retrievedUser.getPassword());
        UserSessionMock.setLoggedUser(retrievedUser);
        // test invalid situations

        try {
            userService.changePassword(null, newPassword);
            fail("UserService.changePassword with null oldPassword should fail.");
        } catch (IncorrectPasswordException ex) {
            // expected
        }

        try {
            userService.changePassword("test_user_password", null);
            fail("UserService.changePassword with null newPassword should fail.");
        } catch (IncorrectPasswordException ex) {
            // expected
        }

        try {
            userService.changePassword("test_user_password", "change_when_old_doesnt_match");
            fail("UserService.changePassword should fail when the oldPassword is not matching the current one.");
        } catch (IncorrectPasswordException ex) {
            // expected
        }
    }

    @org.junit.Test
    public void testFavoriteParameterCRUDOperations() throws Exception {
        User user = new User();
        user.setUsername("test_user");
        fillUser("test_user", user);
        userService.createUser(user);

        Group group = createGroup("group");
        groupService.createGroup(group);

        groupService.addUserToGroup(user, group);
        UserSessionMock.setLoggedUser(user);

        Test test = createTest();
        test.setGroup(group);
        testService.createTest(test);

        FavoriteParameter favoriteParameter1 = new FavoriteParameter();
        fillFavoriteParameter("param1", favoriteParameter1);
        favoriteParameter1.setTest(test);
        FavoriteParameter favoriteParameter2 = new FavoriteParameter();
        fillFavoriteParameter("param2", favoriteParameter2);
        favoriteParameter2.setTest(test);

        userService.createFavoriteParameter(favoriteParameter1);
        userService.createFavoriteParameter(favoriteParameter2);

        List<FavoriteParameter> expectedResult = Arrays.asList(favoriteParameter1, favoriteParameter2);
        List<FavoriteParameter> actualResult = userService.getFavoriteParametersForTest(test);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        // test update
        FavoriteParameter updatedParameter = favoriteParameter1;
        fillFavoriteParameter("updated_param1", updatedParameter);
        updatedParameter.setTest(test);
        userService.updateFavoriteParameter(updatedParameter);

        List<FavoriteParameter> updatedParameters = userService.getFavoriteParametersForTest(test);

        assertTrue(updatedParameters.stream()
                .anyMatch(param -> param.getParameterName().equals(updatedParameter.getParameterName()) && param.getLabel().equals(updatedParameter.getLabel())));

        // test delete
        FavoriteParameter parameterToDelete = favoriteParameter1;
        userService.removeFavoriteParameter(parameterToDelete);

        List<FavoriteParameter> expectedAfterDelete = Arrays.asList(favoriteParameter2);
        List<FavoriteParameter> actualAfterDelete = userService.getFavoriteParametersForTest(test);

        assertEquals(expectedAfterDelete.size(), actualAfterDelete.size());
        assertTrue(expectedAfterDelete.stream().allMatch(expected -> actualAfterDelete.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    @org.junit.Test
    public void testDuplicateUsernames() throws DuplicateEntityException, UnauthorizedException {
        User user1 = new User();
        fillUser("user1", user1);
        user1.setUsername("test_user1");

        User user2 = new User();
        fillUser("user2", user2);
        user2.setUsername("test_user2");

        userService.createUser(user1);
        userService.createUser(user2);

        User duplicateUser = new User();
        fillUser("user3", duplicateUser);
        duplicateUser.setUsername("test_user1");

        try {
            userService.createUser(duplicateUser);
            fail("Duplicate username creation should fail.");
        } catch (DuplicateEntityException ex) {
            // expected
        }

        duplicateUser = user1;
        duplicateUser.setUsername("test_user2");

        try {
            userService.updateUser(duplicateUser);
            fail("Duplicate username update should fail.");
        } catch (DuplicateEntityException ex) {
            // expected
        }
    }

    @org.junit.Test
    public void testUnauthorizedManagement() throws DuplicateEntityException, UnauthorizedException {
        User user = new User();
        user.setUsername("regularUser");
        fillUser("regularUser", user);
        User regularUser = userService.createUser(user);
        UserSessionMock.setLoggedUser(regularUser);

        User newUser = new User();
        newUser.setUsername("new_user");
        fillUser("new_user", newUser);
        try {
            userService.createUser(newUser);
            fail("Regular user shouldn't be able to create a user.");
        } catch (UnauthorizedException ex) {
            // expected
        } finally {
            UserSessionMock.setLoggedUser(adminUser);
        }

        try {
            userService.createUser(newUser);
        } catch (UnauthorizedException ex) {
            fail("Super admin should be able to create a user.");
        }

        newUser.setFirstName("updated_first_name");
        UserSessionMock.setLoggedUser(regularUser);
        try {
            userService.updateUser(newUser);
            fail("Regular user shouldn't be able to update a user.");
        } catch (UnauthorizedException ex) {
            // expected
        }

        // try adding user as a group admin
        UserSessionMock.setLoggedUser(adminUser);
        User tmpUser = new User();
        tmpUser.setUsername("group_admin");
        fillUser("group_admin", tmpUser);
        User groupAdmin = userService.createUser(tmpUser);

        Group group1 = createGroup("group1");
        groupService.createGroup(group1);

        groupService.addUserToGroup(groupAdmin, group1, MembershipType.GROUP_ADMIN);
        UserSessionMock.setLoggedUser(groupAdmin);

        User nextToBeCreated = new User();
        nextToBeCreated.setUsername("next_to_be_created");
        fillUser("next_to_be_created", nextToBeCreated);
        try {
            userService.createUser(nextToBeCreated);
        } catch (UnauthorizedException ex) {
            fail("Group admin should be able to create users.");
        }

        try {
            userService.updateUser(nextToBeCreated);
            fail("Group admin shouldn't be able to update users.");
        } catch (UnauthorizedException ex) {
            // expected
        }

        try {
            userService.removeUser(nextToBeCreated);
            fail("Group admin shouldn't be able to remove users.");
        } catch (UnauthorizedException ex) {
            // expected
        } finally {
            UserSessionMock.setLoggedUser(regularUser);
        }

        try {
            userService.removeUser(nextToBeCreated);
            fail("Regular user shouldn't be able to remove a user.");
        } catch (UnauthorizedException ex) {
            // expected
        }
    }

    /****** HELPER METHODS ******/

    private void assertUser(User expected, User actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getEmail(), actual.getEmail());
    }

    /**
     * Helper method to assign some values to test user entity.
     *
     * @param prefix
     * @param user
     * @return
     */
    private void fillUser(String prefix, User user) {
        user.setFirstName(prefix + "_first_name");
        user.setLastName(prefix + "_last_name");
        user.setPassword(prefix + "_password");
        user.setEmail(prefix + "_email@example.com");
    }

    private void fillProperties(String keyPrefix, String valuePrefix, Map<String, String> properties) {
        properties.put(keyPrefix + "_key1", valuePrefix + "_value1");
        properties.put(keyPrefix + "_key2", valuePrefix + "_value2");
        properties.put(keyPrefix + "_key3", valuePrefix + "_value3");
    }

    private void fillFavoriteParameter(String prefix, FavoriteParameter parameter) {
        parameter.setParameterName(prefix + "_name");
        parameter.setLabel(prefix + "_label");
    }

    private Test createTest() {
        Test test = new Test();
        test.setName("example_test");
        test.setUid("example_test_uid");

        return test;
    }

    private Group createGroup(String prefix) {
        Group group = new Group();
        group.setName(prefix + "_group");

        return group;
    }

}
