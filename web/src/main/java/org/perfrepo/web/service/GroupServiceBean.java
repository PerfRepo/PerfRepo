package org.perfrepo.web.service;

import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.inject.Inject;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class GroupServiceBean implements GroupService {

    @Inject
    private GroupDAO groupDAO;

    @Override
    public Group createGroup(Group group) throws ServiceException {
        return groupDAO.create(group);
    }

    @Override
    public Group updateGroup(Group group) throws ServiceException {
        return groupDAO.merge(group);
    }

    @Override
    public void removeGroup(Group group) throws ServiceException {
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
    public List<Group> getUserGroups(User user) {
        return groupDAO.getUserGroups(user.getId());
    }

    @Override
    public boolean isUserInGroup(Group group, User user) {
        List<Group> groups = getUserGroups(user);
        return groups.stream().anyMatch(g -> g.equals(group));
    }

}
