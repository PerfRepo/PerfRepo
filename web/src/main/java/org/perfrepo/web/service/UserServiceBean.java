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
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.FavoriteParameterDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.dao.UserPropertyDAO;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServiceBean implements UserService {

   @Inject
   private UserDAO userDAO;

   @Inject
   private UserPropertyDAO userPropertyDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private FavoriteParameterDAO favoriteParameterDAO;

   @Override
   public User createUser(User user) throws ServiceException {
      if (user.getId() != null) {
         throw new IllegalArgumentException("Can't create with id");
      }

      user.setPassword(computeMd5(user.getPassword()));

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

      user.setPassword(computeMd5(user.getPassword()));

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
   public void changePassword(String oldPassword, String newPassword, User user) throws ServiceException {
      if (oldPassword == null || newPassword == null) {
         throw new ServiceException("serviceException.changePassword");
      }

      String newPasswordEncrypted = computeMd5(newPassword);
      String oldPasswordEncrypted = computeMd5(oldPassword);

      if (!user.getPassword().equals(oldPasswordEncrypted)) {
         throw new ServiceException("serviceException.passwordDoesntMatch");
      }

      User managedUser = userDAO.get(user.getId());
      managedUser.setPassword(newPasswordEncrypted);
   }

   @Override
   public Map<String, String> getUserProperties(User user) {
      List<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());

      Map<String, String> mapProperties = new HashMap<>();
      properties.stream().forEach(property -> mapProperties.put(property.getName(), property.getValue()));

      return mapProperties;
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

   @Override
   public void addFavoriteParameter(FavoriteParameter parameter, Test test, User user) {
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
   public void removeFavoriteParameter(FavoriteParameter parameter, Test test, User user) {
      FavoriteParameter favoriteParameter = favoriteParameterDAO.get(parameter.getId());
      favoriteParameterDAO.remove(favoriteParameter);
   }

   @Override
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test, User user) {
      if (test.getId() == null) {
         throw new IllegalArgumentException("Test ID cannot be null");
      }

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
