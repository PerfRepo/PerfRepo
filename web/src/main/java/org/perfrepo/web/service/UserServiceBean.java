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
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.*;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.UserSession;

import javax.ejb.*;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServiceBean implements UserService {

   @Inject
   private UserSession userSession;

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

   @Override
   public User createUser(User user) throws ServiceException {
      //TODO: this method needs authorization for this operation, not used at all yet
      if (user.getId() != null) {
         throw new IllegalArgumentException("Can't create with id");
      }

      User newUser = userDAO.create(user);
      return newUser;
   }

   @Override
   public User updateUser(User user) throws ServiceException {
      if (user.getId() == null) {
         throw new IllegalArgumentException("Can't update without id");
      }

      User duplicateUsernameUser = userDAO.findByUsername(user.getUsername());
      if (duplicateUsernameUser != null && !duplicateUsernameUser.getId().equals(user.getId())) {
         throw new ServiceException("serviceException.usernameAlreadyExists", user.getUsername());
      }

      User updatedUser = userDAO.merge(user);
      return updatedUser;
   }

   @Override
   public void removeUser(User user) throws ServiceException {
      if (user.getId() == null) {
         throw new ServiceException("User's ID cannot be null when deleting");
      }
      User managedUser = userDAO.get(user.getId());
      userDAO.remove(managedUser);
   }

   @Override
   public void changePassword(String oldPassword, String newPassword) throws ServiceException {
      if (oldPassword == null || newPassword == null) {
         throw new ServiceException("serviceException.changePassword");
      }

      String newPasswordEncrypted = computeMd5(newPassword);
      String oldPasswordEncrypted = computeMd5(oldPassword);

      User user = userDAO.get(userSession.getLoggedUser().getId());

      if (!user.getPassword().equals(oldPasswordEncrypted)) {
         throw new ServiceException("serviceException.passwordDoesntMatch");
      }

      user.setPassword(newPasswordEncrypted);
      userDAO.merge(user);
   }

   @Override
   public Map<String, String> getUserProperties(User user) {
      List<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());

      Map<String, String> mapProperties = new HashMap<>();
      properties.stream().forEach(property -> mapProperties.put(property.getName(), property.getValue()));

      return mapProperties;
   }

   @Override
   public void addFavoriteParameter(Test test, String paramName, String label) {
      User user = userDAO.get(userSession.getLoggedUser().getId());
      Test testEntity = testDAO.get(test.getId());

      FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), userSession.getLoggedUser().getId());
      if (fp == null) {
         fp = new FavoriteParameter();
      }

      fp.setLabel(label);
      fp.setParameterName(paramName);
      fp.setTest(testEntity);
      fp.setUser(user);

      favoriteParameterDAO.create(fp);
   }

   @Override
   public void removeFavoriteParameter(Test test, String paramName) {
      FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), userSession.getLoggedUser().getId());

      if (fp != null) {
         favoriteParameterDAO.remove(fp);
      }
   }

   @Override
   public User getUser(String username) {
      return userDAO.findByUsername(username);
   }

   @Override
   public User getUser(Long id) {
      return userDAO.get(id);
   }

   @Override
   public Group getGroup(Long id) {
      return groupDAO.get(id);
   }

   @Override
   public List<Group> getGroups() {
      return groupDAO.getAll();
   }

   @Override
   public List<String> getLoggedUserGroupNames() {
      List<String> names = new ArrayList<String>();
      User user = userSession.getLoggedUser();
      Collection<Group> gs = user != null ? userSession.getLoggedUser().getGroups() : Collections.emptyList();
      for (Group group : gs) {
         names.add(group.getName());
      }
      return names;
   }

   @Override
   public List<User> getAllUsers() {
      return userDAO.getAll();
   }

   @Override
   public boolean isLoggedUserInGroup(String guid) {
      User user = userSession.getLoggedUser();
      if (user != null && user.getGroups() != null) {
         for (Group group : user.getGroups()) {
            if (group.getName().equals(guid)) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean isUserInGroup(Long userId, Long groupId) {
      if (userId == null) {
         return false;
      }

      User user = getUser(userId);
      if (user != null && user.getGroups() != null) {
         return user.getGroups().stream().anyMatch(group -> groupId.equals(group.getId()));
      }

      return false;
   }

   @Override
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test) {
      if (test.getId() == null) {
         throw new IllegalArgumentException("Test ID cannot be null");
      }

      User user = userSession.getLoggedUser();
      List<FavoriteParameter> favoriteParameters = favoriteParameterDAO.findByTest(test.getId(), user.getId());
      List<FavoriteParameter> result = favoriteParameters.stream().filter(favoriteParameter -> favoriteParameter.getTest().getId().equals(test.getId())).collect(Collectors.toList());
      return result;
   }

   @Override
   public void updateUserProperties(Map<String, String> properties, User user) throws ServiceException {
      if (user.getId() == null) {
         throw new ServiceException("User ID cannot be null when updating properties.");
      }

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

   private String computeMd5(String string) {
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
