package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.user.UserDto;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;
import org.perfrepo.dto.util.validation.FieldError;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.web.adapter.AuthenticationAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.UnauthorizedException;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

/**
 * Temporary implementation of {@link AuthenticationAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AuthenticationAdapterDummyImpl implements AuthenticationAdapter {

    @Inject
    private Storage storage;

    @Override
    public AuthenticationResult login(LoginCredentialParams credentials) {
        ValidationErrors validationErrors = new ValidationErrors();

        if (credentials == null) {
            throw new UnauthorizedException("Bad login.", validationErrors);
        }

        UserDto user = storage.user().getByUsernameWithPassword(credentials.getUsername());

        if (user == null) {
            validationErrors.addFieldError("username", "The user does not exists.");
            throw new UnauthorizedException("Bad login.", validationErrors);
        }

        if (user.getPassword().equals(credentials.getPassword())) {
            AuthenticationResult authenticationDto = new AuthenticationResult();

            authenticationDto.setUser(storage.user().getByUsername(credentials.getUsername()));

            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, 5);
            Date expiration = c.getTime();
            authenticationDto.setExpiration(expiration);

            String token = credentials.getUsername() + ":" + expiration.getTime();
            authenticationDto.setToken(token);
            storage.token().saveToken(token);

            return authenticationDto;
        } else {
            validationErrors.addFieldError("password", "Bad password.");
        }

        throw new UnauthorizedException("Bad password.", validationErrors);
    }

    @Override
    public void logout(String token) {
        storage.token().removeToken(token);
    }
}
