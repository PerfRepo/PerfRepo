package org.perfrepo.web.service;

import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GroupServiceBean implements GroupService {

    @Inject
    private GroupDAO groupDAO;

    @Inject
    private UserDAO userDAO;

    @Override
    public Group createGroup(Group group) throws DuplicateEntityException {
        if (getGroup(group.getName()) != null) {
            throw new DuplicateEntityException("group.duplicateName", group.getName());
        }

        return groupDAO.create(group);
    }

    @Override
    public Group updateGroup(Group group) throws DuplicateEntityException {
        Group possibleDuplicate = getGroup(group.getName());
        if (possibleDuplicate != null && !possibleDuplicate.getId().equals(group.getId())) {
            throw new DuplicateEntityException("group.duplicateName", group.getName());
        }

        return groupDAO.merge(group);
    }

    @Override
    public void removeGroup(Group group) {
        Group managedGroup = groupDAO.get(group.getId());
        groupDAO.remove(managedGroup);
    }

    @Override
    public Group getGroup(Long id) {
        return groupDAO.get(id);
    }

    @Override
    public Group getGroup(String name) {
        return groupDAO.findByName(name);
    }

    @Override
    public List<Group> getAllGroups() {
        return groupDAO.getAll();
    }

    @Override
    public Set<Group> getUserGroups(User user) {
        return groupDAO.getUserGroups(user);
    }

    @Override
    public boolean isUserInGroup(User user, Group group) {
        Set<Group> groups = getUserGroups(user);
        return groups.contains(group);
    }

    @Override
    public void addUserToGroup(User user, Group group) {
        User managedUser = userDAO.get(user.getId());
        Group managedGroup = groupDAO.get(group.getId());

        managedUser.getGroups().add(managedGroup);
        managedGroup.getUsers().add(managedUser);
    }
}
