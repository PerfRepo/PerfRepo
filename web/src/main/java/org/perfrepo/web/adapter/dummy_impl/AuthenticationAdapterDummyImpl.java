package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.user.UserDto;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;
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
        if (credentials == null) {
            throw new UnauthorizedException("Bad login.");
        }

        UserDto user = storage.user().getByUsernameWithPassword(credentials.getUsername());

        if (user == null) {
            throw new UnauthorizedException("Bad login.");
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
        }

        throw new UnauthorizedException("Bad password.");
    }

    @Override
    public void logout(String token) {
        storage.token().removeToken(token);
    }
}
