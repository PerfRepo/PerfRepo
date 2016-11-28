package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.util.TestUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class GroupServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private GroupService groupService;

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment();
    }

    @After
    public void cleanUp() {
        for (User user: userService.getAllUsers()) {
            userService.removeUser(user);
        }

        for (Group group: groupService.getAllGroups()) {
            groupService.removeGroup(group);
        }
    }

    @org.junit.Test
    public void testUserCRUDOperations() throws DuplicateEntityException {
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
    public void testGetAllGroups() throws DuplicateEntityException {
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
    public void testGetUserGroupsAndIsUserInGroup() throws DuplicateEntityException {
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

        Set<Group> expectedResult = new HashSet<>(Arrays.asList(group2, group3));
        Set<Group> actualResult = groupService.getUserGroups(user);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        assertFalse(groupService.isUserInGroup(user, group1));
        assertTrue(groupService.isUserInGroup(user, group2));
        assertTrue(groupService.isUserInGroup(user, group3));
    }

    @org.junit.Test
    public void testAssignUserToGroup() throws DuplicateEntityException {
        Group group1 = new Group();
        fillGroup("group1", group1);

        Group group2 = new Group();
        fillGroup("group2", group2);

        groupService.createGroup(group1);
        groupService.createGroup(group2);

        User user = createUser();
        Set<Group> userGroups = new HashSet<>(Arrays.asList(group1));
        user.setGroups(userGroups);
        group1.setUsers(new HashSet<>(Arrays.asList(user)));
        userService.createUser(user);

        Set<Group> expectedResult = new HashSet<>(Arrays.asList(group1));
        Set<Group> actualResult = groupService.getUserGroups(user);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        groupService.addUserToGroup(user, group2);

        assertTrue(groupService.isUserInGroup(user, group1));
        assertTrue(groupService.isUserInGroup(user, group2));
    }

    @Test
    public void testDuplicateNames() throws DuplicateEntityException {
        Group group1 = new Group();
        fillGroup("group1", group1);

        Group group2 = new Group();
        fillGroup("group2", group2);

        groupService.createGroup(group1);
        groupService.createGroup(group2);

        Group duplicateGroup = new Group();
        fillGroup("group1", duplicateGroup);

        try {
            groupService.createGroup(duplicateGroup);
            fail("Duplicate group name creation should fail.");
        } catch (DuplicateEntityException ex) {
            // expected
        }

        duplicateGroup = group1;
        fillGroup("group2", duplicateGroup);

        try {
            groupService.updateGroup(duplicateGroup);
            fail("Duplicate group name update should fail.");
        } catch (DuplicateEntityException ex) {
            // expected
        }
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
