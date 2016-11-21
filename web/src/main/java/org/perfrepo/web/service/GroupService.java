package org.perfrepo.web.service;

import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.ServiceException;

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
     * @throws ServiceException
     */
    public Group createGroup(Group group) throws ServiceException;

    /**
     * Updates group
     *
     * @param group
     * @return group
     * @throws ServiceException
     */
    public Group updateGroup(Group group) throws ServiceException;

    /**
     * Deletes group
     *
     * @param group
     */
    public void removeGroup(Group group) throws ServiceException;

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
     * Retrieves all user groups
     *
     * @param user
     * @return
     */
    public List<Group> getUserGroups(User user);

    /**
     * Retrieves if user is assign in defined group
     *
     * @param group
     * @param user
     * @return boolean
     */
    public boolean isUserInGroup(Group group, User user);
}
