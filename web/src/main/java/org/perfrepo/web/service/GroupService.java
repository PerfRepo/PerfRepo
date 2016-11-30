package org.perfrepo.web.service;

import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.Membership.MembershipType;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;

import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */

public interface GroupService {

    /**
     * Create new group.
     *
     * @param group
     * @return group
     */
    public Group createGroup(Group group) throws DuplicateEntityException;

    /**
     * Updates group
     *
     * @param group
     * @return group
     */
    public Group updateGroup(Group group) throws DuplicateEntityException;

    /**
     * Deletes group
     *
     * @param group
     */
    public void removeGroup(Group group);

    /**
     * Retrieves group
     *
     * @param id
     * @return group
     */
    public Group getGroup(Long id);

    /**
     * Retrieves group by name
     *
     * @param name
     * @return group
     */
    public Group getGroup(String name);

    /**
     * Retrieves all groups
     *
     * @return
     */
    public List<Group> getAllGroups();

    /**
     * Retrieves if user is assign in defined group
     *
     * @param user
     * @param group
     * @return boolean
     */
    public boolean isUserInGroup(User user, Group group);

    /**
     * Adds user to group as a regular user.
     *
     * @param user
     * @param group
     */
    public void addUserToGroup(User user, Group group);

    /**
     * Adds user to group.
     *
     * @param user
     * @param group
     */
    public void addUserToGroup(User user, Group group, MembershipType membershipType);
}
