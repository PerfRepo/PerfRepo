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
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class GroupServiceTest {

    @Inject
    private UserService userService;

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
        for (User user: userService.getAllUsers()) {
            userService.removeUser(user);
        }

        for (Group group: groupService.getAllGroups()) {
            groupService.removeGroup(group);
        }
    }

    @org.junit.Test
    public void testUserCRUDOperations() throws Exception {
        Group group = new Group();
        fillGroup("group", group);

        groupService.createGroup(group);

        Group createdGroup = groupService.getGroup(group.getId());
        Group createdGroupByName = groupService.getGroup(group.getName());

        assertNotNull(createdGroup);
        assertNotNull(createdGroupByName);
        assertGroup(group, createdGroup);
        assertGroup(group, createdGroupByName);

        // test update
        Group groupToUpdate = createdGroup;
        fillGroup("updated_group", groupToUpdate);

        Group updatedGroup = groupService.updateGroup(groupToUpdate);
        assertGroup(groupToUpdate, updatedGroup);

        // test delete
        Group groupToDelete = updatedGroup;
        groupService.removeGroup(groupToDelete);
        assertNull(groupService.getGroup(groupToDelete.getId()));
    }

    @org.junit.Test
    public void testGetAllGroups() throws Exception {
        Group group1 = new Group();
        fillGroup("group1", group1);

        Group group2 = new Group();
        fillGroup("group2", group2);

        groupService.createGroup(group1);
        groupService.createGroup(group2);

        List<Group> expectedResult = Arrays.asList(group1, group2);
        List<Group> actualResult = groupService.getAllGroups();

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    @org.junit.Test
    public void testGetUserGroupsAndIsUserInGroup() throws Exception {
        Group group1 = new Group();
        fillGroup("group1", group1);

        Group group2 = new Group();
        fillGroup("group2", group2);

        Group group3 = new Group();
        fillGroup("group3", group3);

        groupService.createGroup(group1);
        groupService.createGroup(group2);
        groupService.createGroup(group3);

        User user = createUser();
        Set<Group> userGroups = new HashSet<>(Arrays.asList(group2, group3));
        user.setGroups(userGroups);
        group2.setUsers(new HashSet<>(Arrays.asList(user)));
        group3.setUsers(new HashSet<>(Arrays.asList(user)));
        userService.createUser(user);

        List<Group> expectedResult = Arrays.asList(group2, group3);
        List<Group> actualResult = groupService.getUserGroups(user);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        assertFalse(groupService.isUserInGroup(group1, user));
        assertTrue(groupService.isUserInGroup(group2, user));
        assertTrue(groupService.isUserInGroup(group3, user));
    }

    /****** HELPER METHODS ******/

    private void assertGroup(Group expected, Group actual) {
        assertEquals(expected.getName(), actual.getName());
    }

    private void fillGroup(String prefix, Group group) {
        group.setName(prefix + "_name");
    }

    private User createUser() {
        User user = new User();
        user.setFirstName("test_first_name");
        user.setLastName("test_last_name");
        user.setEmail("test@email.com");
        user.setUsername("test_username");
        user.setPassword("test_password");

        return user;
    }

}
