/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.service;

import org.apache.commons.codec.binary.Base64;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.UserProperty;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.Membership;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.FavoriteParameterDAO;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.dao.MembershipDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.dao.UserPropertyDAO;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.IncorrectPasswordException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServiceBean implements UserService {

   @Inject
   private UserDAO userDAO;

   @Inject
   private GroupDAO groupDAO;

   @Inject
   private UserPropertyDAO userPropertyDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private FavoriteParameterDAO favoriteParameterDAO;

   @Inject
   private MembershipDAO membershipDAO;

   @Inject
   private UserSession userSession;

   @Override
   public User createUser(User user) throws DuplicateEntityException, UnauthorizedException {
      if (!userSession.getLoggedUser().isSuperAdmin() && !isUserGroupAdmin(userSession.getLoggedUser())) {
         throw new UnauthorizedException("user.userNotAllowedToCreateUsers", userSession.getLoggedUser().getUsername());
      }

      if (getUser(user.getUsername()) != null) {
         throw new DuplicateEntityException("user.usernameAlreadyExists", user.getUsername());
      }

      user.setPassword(computeMd5(user.getPassword()));

      User newUser = userDAO.create(user);
      return newUser;
   }

   @Override
   public User updateUser(User user) throws DuplicateEntityException, UnauthorizedException {
      if (!userSession.getLoggedUser().isSuperAdmin()) {
         throw new UnauthorizedException("user.userNotAllowedToUpdateOrRemoveUsers", userSession.getLoggedUser().getUsername());
      }

      User possibleDuplicate = getUser(user.getUsername());
      if (possibleDuplicate != null && !possibleDuplicate.getId().equals(user.getId())) {
         throw new DuplicateEntityException("user.usernameAlreadyExists", user.getUsername());
      }

      user.setPassword(computeMd5(user.getPassword()));

      User updatedUser = userDAO.merge(user);
      return updatedUser;
   }

   @Override
   public void removeUser(User user) throws UnauthorizedException {
      if (!userSession.getLoggedUser().isSuperAdmin()) {
         throw new UnauthorizedException("user.userNotAllowedToUpdateOrRemoveUsers", userSession.getLoggedUser().getUsername());
      }

      User managedUser = userDAO.get(user.getId());
      userDAO.remove(managedUser);
   }

   @Override
   public User getUser(Long id) {
      return userDAO.get(id);
   }

   @Override
   public User getUser(String username) {
      return userDAO.findByUsername(username);
   }

   @Override
   public List<User> getAllUsers() {
      return userDAO.getAll();
   }

   @Override
   public Set<Group> getUserGroups(User user) {
      User managedUser = userDAO.get(user.getId());
      return groupDAO.getUserGroups(managedUser);
   }

   @Override
   public void changePassword(String oldPassword, String newPassword, User user) throws IncorrectPasswordException {
      if (oldPassword == null || newPassword == null) {
         throw new IncorrectPasswordException("user.oldOrNewPasswordNotSet");
      }

      String newPasswordEncrypted = computeMd5(newPassword);
      String oldPasswordEncrypted = computeMd5(oldPassword);

      if (!user.getPassword().equals(oldPasswordEncrypted)) {
         throw new IncorrectPasswordException("user.oldPasswordDoesntMatch");
      }

      User managedUser = userDAO.get(user.getId());
      managedUser.setPassword(newPasswordEncrypted);
   }

   @Override
   public boolean isUserGroupAdmin(User user) {
      User managedUser = userDAO.get(user.getId());
      Set<Group> groups = getUserGroups(managedUser);

      for (Group group: groups) {
         Membership membership = membershipDAO.getMembership(managedUser, group);
         if (membership.getType() == Membership.MembershipType.GROUP_ADMIN) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isUserGroupAdmin(User user, Group group) {
      User managedUser = userDAO.get(user.getId());
      Group managedGroup = groupDAO.get(group.getId());

      Membership membership = membershipDAO.getMembership(managedUser, managedGroup);
      if (membership == null || membership.getType() != Membership.MembershipType.GROUP_ADMIN) {
         return false;
      }

      return true;
   }

   @Override
   public Map<String, String> getUserProperties(User user) {
      List<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());

      Map<String, String> mapProperties = new HashMap<>();
      properties.stream().forEach(property -> mapProperties.put(property.getName(), property.getValue()));

      return mapProperties;
   }

   @Override
   public void updateUserProperties(Map<String, String> properties, User user) {
      userPropertyDAO.deletePropertiesFromUser(user.getId());

      User managedUser = userDAO.get(user.getId());
      for (String key: properties.keySet()) {
         UserProperty property = new UserProperty();
         property.setName(key);
         property.setValue(properties.get(key));
         property.setUser(managedUser);
         userPropertyDAO.create(property);
      }
   }

   @Override
   public void createFavoriteParameter(FavoriteParameter parameter, Test test, User user) {
      User managedUser = userDAO.get(user.getId());
      Test managedTest = testDAO.get(test.getId());

      parameter.setTest(managedTest);
      parameter.setUser(managedUser);

      favoriteParameterDAO.create(parameter);
   }

   @Override
   public void updateFavoriteParameter(FavoriteParameter parameter, Test test, User user) {
      User managedUser = userDAO.get(user.getId());
      Test managedTest = testDAO.get(test.getId());

      parameter.setTest(managedTest);
      parameter.setUser(managedUser);

      favoriteParameterDAO.merge(parameter);
   }

   @Override
   public void removeFavoriteParameter(FavoriteParameter parameter) {
      FavoriteParameter favoriteParameter = favoriteParameterDAO.get(parameter.getId());
      favoriteParameterDAO.remove(favoriteParameter);
   }

   @Override
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test, User user) {
      List<FavoriteParameter> favoriteParameters = favoriteParameterDAO.findByTest(test.getId(), user.getId());
      List<FavoriteParameter> result = favoriteParameters.stream().filter(favoriteParameter -> favoriteParameter.getTest().getId().equals(test.getId())).collect(Collectors.toList());
      return result;
   }

   /**
    * Package-private access for testing purpose.
    * @param string
    * @return
     */
   static String computeMd5(String string) {
      MessageDigest md = null;

      try {
         md = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException ex) {
         throw new RuntimeException(ex);
      }

      try {
         md.update(string.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }

      return Base64.encodeBase64String(md.digest());
   }
}
