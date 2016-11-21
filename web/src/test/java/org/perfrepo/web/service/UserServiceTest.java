package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.runner.RunWith;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");

        war.addPackages(true, "org.perfrepo.web");
        war.addPackages(true, "org.perfrepo.model");

        war.addAsLibraries(Maven.resolver().resolve("commons-codec:commons-codec:1.9").withTransitivity().asFile());
        war.addAsLibraries(Maven.resolver().resolve("org.antlr:antlr:3.5.2").withTransitivity().asFile());
        war.addAsLibraries(Maven.resolver().resolve("org.apache.maven:maven-artifact:3.0.3").withTransitivity().asFile());

        war.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
        war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

        return war;
    }

    @After
    public void cleanUp() throws Exception {
        // deletion of favorite parameters is done via casdace on foreign key in database
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
        Map<String, String> properties = new HashMap<>();
        fillProperties("version_1", "version_1", properties);
        userService.updateUserProperties(properties, user);

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
        userService.updateUserProperties(propertiesToUpdate, user);

        Map<String, String> updatedProperties = userService.getUserProperties(createdUser);
        assertEquals(propertiesToUpdate, updatedProperties);

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

        List<User> expectedResult = Arrays.asList(user1, user2);
        List<User> actualResult = userService.getAllUsers();

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    @org.junit.Test
    public void testChangePassword() throws Exception {
        User user = new User();
        user.setUsername("test_user");
        fillUser("test_user", user);

        userService.createUser(user);

        String newPassword = "nastyPassword9";
        userService.changePassword("test_user_password", newPassword, user);

        User retrievedUser = userService.getUser(user.getId());
        assertEquals(UserServiceBean.computeMd5(newPassword), retrievedUser.getPassword());
    }

    @org.junit.Test
    public void testFavoriteParameterCRUDOperations() throws Exception {
        User user = new User();
        user.setUsername("test_user");
        fillUser("test_user", user);
        userService.createUser(user);

        Group group = createGroup();
        groupService.createGroup(group);

        Test test = createTest();
        test.setGroup(group);
        testService.createTest(test);

        FavoriteParameter favoriteParameter1 = new FavoriteParameter();
        fillFavoriteParameter("param1", favoriteParameter1);
        FavoriteParameter favoriteParameter2 = new FavoriteParameter();
        fillFavoriteParameter("param2", favoriteParameter2);

        userService.createFavoriteParameter(favoriteParameter1, test, user);
        userService.createFavoriteParameter(favoriteParameter2, test, user);

        List<FavoriteParameter> expectedResult = Arrays.asList(favoriteParameter1, favoriteParameter2);
        List<FavoriteParameter> actualResult = userService.getFavoriteParametersForTest(test, user);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        // test update
        FavoriteParameter updatedParameter = favoriteParameter1;
        fillFavoriteParameter("updated_param1", updatedParameter);
        userService.updateFavoriteParameter(updatedParameter, test, user);

        List<FavoriteParameter> updatedParameters = userService.getFavoriteParametersForTest(test, user);

        assertTrue(updatedParameters.stream()
                .anyMatch(param -> param.getParameterName().equals(updatedParameter.getParameterName()) && param.getLabel().equals(updatedParameter.getLabel())));

        // test delete
        FavoriteParameter parameterToDelete = favoriteParameter1;
        userService.removeFavoriteParameter(parameterToDelete);

        List<FavoriteParameter> expectedAfterDelete = Arrays.asList(favoriteParameter2);
        List<FavoriteParameter> actualAfterDelete = userService.getFavoriteParametersForTest(test, user);

        assertEquals(expectedAfterDelete.size(), actualAfterDelete.size());
        assertTrue(expectedAfterDelete.stream().allMatch(expected -> actualAfterDelete.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
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

    private Group createGroup() {
        Group group = new Group();
        group.setName("example_group");

        return group;
    }

}
