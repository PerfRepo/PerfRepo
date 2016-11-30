package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.Membership;
import org.perfrepo.model.user.Membership.MembershipType;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.util.TestUtils;
import org.perfrepo.web.service.util.UserSessionMock;

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

    private User adminUser;
    
    @Before
    public void init() throws DuplicateEntityException, UnauthorizedException {
        adminUser = createUser("admin");
        adminUser.setType(User.UserType.SUPER_ADMIN);
        UserSessionMock.setLoggedUser(adminUser); // hack, because we need some super admin to create a super admin :)
        userService.createUser(adminUser);
        UserSessionMock.setLoggedUser(adminUser);
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
    }

    @org.junit.Test
    public void testUserCRUDOperations() throws DuplicateEntityException, UnauthorizedException {
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
    public void testGetAllGroups() throws DuplicateEntityException, UnauthorizedException {
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
    public void testGetUserGroupsAndIsUserInGroup() throws DuplicateEntityException, UnauthorizedException {
        Group group1 = new Group();
        fillGroup("group1", group1);

        Group group2 = new Group();
        fillGroup("group2", group2);

        Group group3 = new Group();
        fillGroup("group3", group3);

        groupService.createGroup(group1);
        groupService.createGroup(group2);
        groupService.createGroup(group3);

        User user = createUser("test");
        userService.createUser(user);
        groupService.addUserToGroup(user, group2);
        groupService.addUserToGroup(user, group3);

        Set<Group> expectedResult = new HashSet<>(Arrays.asList(group2, group3));
        Set<Group> actualResult = userService.getUserGroups(user);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        assertFalse(groupService.isUserInGroup(user, group1));
        assertTrue(groupService.isUserInGroup(user, group2));
        assertTrue(groupService.isUserInGroup(user, group3));
    }

    @org.junit.Test
    public void testAssignUserToGroup() throws DuplicateEntityException, UnauthorizedException {
        Group group1 = new Group();
        fillGroup("group1", group1);

        Group group2 = new Group();
        fillGroup("group2", group2);

        groupService.createGroup(group1);
        groupService.createGroup(group2);

        User user = createUser("test");
        userService.createUser(user);
        groupService.addUserToGroup(user, group1);

        Set<Group> expectedResult = new HashSet<>(Arrays.asList(group1));
        Set<Group> actualResult = userService.getUserGroups(user);

        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));

        groupService.addUserToGroup(user, group2);

        assertTrue(groupService.isUserInGroup(user, group1));
        assertTrue(groupService.isUserInGroup(user, group2));

        groupService.removeUserFromGroup(user, group2);

        assertFalse(groupService.isUserInGroup(user, group2));
    }

    @Test
    public void testDuplicateNames() throws DuplicateEntityException, UnauthorizedException {
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

    @Test
    public void testUnauthorizedManagement() throws DuplicateEntityException, UnauthorizedException {
        User user = createUser("test");
        User regularUser = userService.createUser(user);
        UserSessionMock.setLoggedUser(regularUser);

        Group group1 = new Group();
        fillGroup("group1", group1);
        try {
            groupService.createGroup(group1);
            fail("Regular user shouldn't be able to create a group.");
        } catch (UnauthorizedException ex) {
            // expected
        } finally {
            UserSessionMock.setLoggedUser(adminUser);
        }

        Group group2 = new Group();
        fillGroup("group2", group2);
        try {
            groupService.createGroup(group1);
            groupService.createGroup(group2);
        } catch (UnauthorizedException ex) {
            fail("Super admin should be able to create a group.");
        }

        group1.setName("updated_group");
        UserSessionMock.setLoggedUser(regularUser);
        try {
            groupService.updateGroup(group1);
            fail("Regular user shouldn't be able to update a group.");
        } catch (UnauthorizedException ex) {
            // expected
        }

        try {
            groupService.addUserToGroup(regularUser, group1);
            fail("Regular user shouldn't be able to add somebody to group.");
        } catch (UnauthorizedException ex) {
            // expected
        }

        try {
            groupService.removeUserFromGroup(regularUser, group1);
            fail("Regular user shouldn't be able to remove somebody from group.");
        } catch (UnauthorizedException ex) {
            // expected
        }

        // try adding user to group as a group admin
        UserSessionMock.setLoggedUser(adminUser);
        User tmpUser = createUser("group_admin");
        User groupAdmin = userService.createUser(tmpUser);
        groupService.addUserToGroup(groupAdmin, group1, MembershipType.GROUP_ADMIN);
        UserSessionMock.setLoggedUser(groupAdmin);

        try {
            groupService.addUserToGroup(regularUser, group1);
        } catch (UnauthorizedException ex) {
            fail("Group admin should be able to add other users to his group.");
        }

        try {
            groupService.addUserToGroup(regularUser, group2);
            fail("Group admin shouldn't be able to add users to group that he's not group admin of.");
        } catch (UnauthorizedException ex) {
            // expected
        }

        try {
            groupService.removeUserFromGroup(regularUser, group1);
        } catch (UnauthorizedException ex) {
            fail("Group admin should be able to remove other users from his group.");
        }

        UserSessionMock.setLoggedUser(adminUser);
        groupService.addUserToGroup(regularUser, group2);
        UserSessionMock.setLoggedUser(groupAdmin);

        try {
            groupService.removeUserFromGroup(regularUser, group2);
            fail("Group admin shouldn't be able to remove users from group that he's not group admin of.");
        } catch (UnauthorizedException ex) {
            // expected
        } finally {
            UserSessionMock.setLoggedUser(regularUser);
        }

        try {
            groupService.removeGroup(group1);
            fail("Regular user shouldn't be able to remove a group.");
        } catch (UnauthorizedException ex) {
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

    private User createUser(String prefix) {
        User user = new User();
        user.setFirstName(prefix + "_first_name");
        user.setLastName(prefix + "_last_name");
        user.setEmail(prefix + "@email.com");
        user.setUsername(prefix + "_username");
        user.setPassword(prefix + "_password");

        return user;
    }

}
