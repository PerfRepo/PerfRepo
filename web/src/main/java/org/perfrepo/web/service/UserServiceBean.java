/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.service;

import org.apache.commons.codec.binary.Base64;

import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.UserProperty;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.model.util.EntityUtils;
import org.perfrepo.web.dao.FavoriteParameterDAO;
import org.perfrepo.web.dao.GroupDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.dao.UserPropertyDAO;
import org.perfrepo.web.service.exceptions.ServiceException;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
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

	@Resource
	private SessionContext sessionContext;

	@Override
	public User createUser(User user) throws ServiceException {
		//TODO: this method needs authorization for this operation, not used at all yet
		if (user.getId() != null) {
			throw new IllegalArgumentException("Can't create with id");
		}

		User newUser = userDAO.create(user).clone();
		newUser.setProperties(new ArrayList<UserProperty>(0));
		return newUser;
	}

	@Override
	public User updateUser(User user) throws ServiceException {
		if (user.getId() == null) {
			throw new IllegalArgumentException("Can't update without id");
		}
		User loggedUser = getLoggedUser();
		if (!user.getId().equals(loggedUser.getId())) {
			throw new SecurityException("Only logged-in user can change his own properties");
		}

		User duplicateUsernameUser = userDAO.findByUsername(user.getUsername());
		if (duplicateUsernameUser != null && duplicateUsernameUser.getId() != user.getId()) {
			throw new ServiceException(ServiceException.Codes.USERNAME_ALREADY_EXISTS, user.getUsername());
		}

		User updatedUser = userDAO.update(user);
		return updatedUser;
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) throws ServiceException {
		if (oldPassword == null || newPassword == null) {
			throw new ServiceException(ServiceException.Codes.PASSWORD_IS_EMPTY);
		}

		String newPasswordEncrypted = computeMd5(newPassword);
		String oldPasswordEncrypted = computeMd5(oldPassword);

		User user = userDAO.get(getLoggedUser().getId());

		if (!user.getPassword().equals(oldPasswordEncrypted)) {
			throw new ServiceException(ServiceException.Codes.PASSWORD_DOESNT_MATCH);
		}

		user.setPassword(newPasswordEncrypted);
		userDAO.update(user);
	}

	@Override
	public Map<String, String> getUserProperties() {
		List<UserProperty> ups = userPropertyDAO.findByUserId(getLoggedUser().getId());
		return transformToMap(ups);
	}

	@Override
	public void addFavoriteParameter(Test test, String paramName, String label) throws ServiceException {
		User user = userDAO.get(getLoggedUser().getId());
		Test testEntity = testDAO.get(test.getId());

		FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), getLoggedUser().getId());
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
	public void addUserProperty(String name, String value) {
		User user = userDAO.get(getLoggedUser().getId());
		UserProperty up = userPropertyDAO.findByUserIdAndName(user.getId(), name);
		if (up == null) {
			up = new UserProperty();
		}
		up.setName(name);
		up.setValue(value);
		up.setUser(user);
		userPropertyDAO.create(up);
	}

	@Override
	public void removeFavoriteParameter(Test test, String paramName) throws ServiceException {
		FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), getLoggedUser().getId());

		if (fp != null) {
			favoriteParameterDAO.remove(fp);
		}
	}

	@Override
	public User getFullUser(Long id) {
		User user = userDAO.get(id);
		if (user == null) {
			return null;
		}

		Collection<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());
		Collection<FavoriteParameter> favoriteParameters = user.getFavoriteParameters();
		Collection<Group> groups = user.getGroups();

		User clonedUser = user.clone();
		clonedUser.setProperties(EntityUtils.clone(properties));
		clonedUser.setFavoriteParameters(EntityUtils.clone(favoriteParameters));
		clonedUser.setGroups(EntityUtils.clone(groups));

		return clonedUser;
	}

   @Override
   public User getFullUser(String username) {
      User user = userDAO.findByUsername(username);
      if (user == null) {
         return null;
      }

      return getFullUser(user.getId());
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
		User user = getLoggedUser();
		Collection<Group> gs = user != null ? getLoggedUser().getGroups() : Collections.emptyList();
		for (Group group : gs) {
			names.add(group.getName());
		}
		return names;
	}

	@Override
	public List<User> getUsers() {
		return userDAO.getAll();
	}

	@Override
	public User getLoggedUser() {
		Principal principal = sessionContext.getCallerPrincipal();
		User user = null;
		if (principal != null) {
			user = userDAO.findByUsername(principal.getName());
		}
		return user;
	}

	@Override
	public boolean isLoggedUserInGroup(String guid) {
		User user = getLoggedUser();
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
		if (userId != null) {
			User user = getUser(userId);
			if (user != null && user.getGroups() != null) {
				for (Group group : user.getGroups()) {
					if (groupId.equals(group.getId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<FavoriteParameter> getFavoriteParametersForTest(Test test) {
		User user = getLoggedUser();
		List<FavoriteParameter> result = new ArrayList<FavoriteParameter>();
		for (FavoriteParameter favoriteParameter : user.getFavoriteParameters()) {
			if (favoriteParameter.getTest().getId().equals(test.getId())) {
				result.add(favoriteParameter);
			}
		}
		return result;
	}

	private Map<String, String> transformToMap(List<UserProperty> ups) {
		Map<String, String> map = new HashMap<String, String>();
		for (UserProperty up : ups) {
			map.put(up.getName(), up.getValue());
		}
		return map;
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
