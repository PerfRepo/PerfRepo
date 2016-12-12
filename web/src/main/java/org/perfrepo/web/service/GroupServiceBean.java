package org.perfrepo.web.service;

import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.Membership;
import org.perfrepo.model.user.Membership.MembershipType;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.dao.MembershipDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.session.UserSession;

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

    @Inject
    private MembershipDAO membershipDAO;

    @Inject
    private UserService userService;

    @Inject
    private UserSession userSession;

    @Override
    public Group createGroup(Group group) throws DuplicateEntityException, UnauthorizedException {
        if (!userSession.getLoggedUser().isSuperAdmin()) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        if (getGroup(group.getName()) != null) {
            throw new DuplicateEntityException("group.duplicateName", group.getName());
        }

        return groupDAO.create(group);
    }

    @Override
    public Group updateGroup(Group group) throws DuplicateEntityException, UnauthorizedException {
        if (!userSession.getLoggedUser().isSuperAdmin()) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        Group possibleDuplicate = getGroup(group.getName());
        if (possibleDuplicate != null && !possibleDuplicate.getId().equals(group.getId())) {
            throw new DuplicateEntityException("group.duplicateName", group.getName());
        }

        return groupDAO.merge(group);
    }

    @Override
    public void removeGroup(Group group) throws UnauthorizedException {
        if (!userSession.getLoggedUser().isSuperAdmin()) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

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
    public boolean isUserInGroup(User user, Group group) {
        Set<Group> groups = userService.getUserGroups(user);
        return groups.contains(group);
    }

    @Override
    public void addUserToGroup(User user, Group group) throws UnauthorizedException {
        addUserToGroup(user, group, MembershipType.REGULAR_USER);
    }

    @Override
    public void addUserToGroup(User user, Group group, MembershipType membershipType) throws UnauthorizedException {
        if (!userSession.getLoggedUser().isSuperAdmin() && !userService.isUserGroupAdmin(userSession.getLoggedUser(), group)) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        User managedUser = userDAO.get(user.getId());
        Group managedGroup = groupDAO.get(group.getId());

        Membership membership = new Membership();
        membership.setUser(managedUser);
        membership.setGroup(managedGroup);
        membership.setType(membershipType);

        membershipDAO.create(membership);
    }

    @Override
    public void removeUserFromGroup(User user, Group group) throws UnauthorizedException {
        if (!userSession.getLoggedUser().isSuperAdmin() && !userService.isUserGroupAdmin(userSession.getLoggedUser(), group)) {
            throw new UnauthorizedException("authorization.group.notAllowedToManageGroups", userSession.getLoggedUser().getUsername());
        }

        User managedUser = userDAO.get(user.getId());
        Group managedGroup = groupDAO.get(group.getId());

        Membership membership = membershipDAO.getMembership(managedUser, managedGroup);
        membershipDAO.remove(membership);
    }
}
