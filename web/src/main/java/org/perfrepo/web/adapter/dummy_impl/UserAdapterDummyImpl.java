package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.user.UserDto;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.web.adapter.UserAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;

import javax.inject.Inject;
import java.util.List;

/**
 * Temporary implementation of {@link UserAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UserAdapterDummyImpl implements UserAdapter {

    @Inject
    private Storage storage;

    @Override
    public List<UserDto> getAllUsers() {
        return storage.user().getAll();
    }

    @Override
    public UserDto getUser(Long id) {
        UserDto user = storage.user().getById(id);

        if (user == null) {
            throw new NotFoundException("User does not exist.");
        }

        return user;
    }

    @Override
    public UserDto updateUser(UserDto user) {
        UserDto updated = storage.user().getByIdWithPassword(user.getId());
        if (updated == null) {
            throw new NotFoundException("User does not exist.");
        }
        // validate
        validate(user, updated.getPassword());

        updated.setFirstName(user.getFirstName());
        updated.setLastName(user.getLastName());
        updated.setEmail(user.getEmail());
        if (updated.isPasswordChange()) {
            updated.setPassword(user.getNewPassword());
        }

        return updated;
    }

    private void validate(UserDto user, String oldPassword) {
        ValidationErrors validation = new ValidationErrors();

        // first name
        if (user.getFirstName() == null) {
            validation.addFieldError("firstName", "First name is a required field");
        } else if (user.getFirstName().trim().length() < 3) {
            validation.addFieldError("firstName", "First name must be at least three characters.");
        }
        // last name
        if (user.getLastName() == null) {
            validation.addFieldError("lastName", "Last name is a required field");
        } else if (user.getLastName().trim().length() < 3) {
            validation.addFieldError("lastName", "Last name must be at least three characters.");
        }
        // email
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            validation.addFieldError("email", "Email is a required field");
        }

        if (user.isPasswordChange()) {
            if (user.getPassword() == null || !user.getPassword().equals(oldPassword)) {
                validation.addFieldError("password", "Password is wrong");
            } else {
                boolean newPassOk = true;
                if (user.getNewPassword() == null || user.getNewPassword().trim().length() < 5) {
                    newPassOk = false;
                    validation.addFieldError("newPassword", "New password must be at least 5 characters.");
                }
                if (user.getNewPasswordAgain() == null || user.getNewPasswordAgain().trim().length() < 5) {
                    newPassOk = false;
                    validation.addFieldError("newPasswordAgain", "New password must be at least 5 characters.");
                }
                if (newPassOk && !user.getNewPassword().equals(user.getNewPasswordAgain())) {
                    validation.addFieldError("newPasswordAgain", "New password not match.");
                }
            }
        }

        if (validation.hasErrors()) {
            throw new ValidationException("User contains validation errors, please fix it.", validation);
        }
    }
}
