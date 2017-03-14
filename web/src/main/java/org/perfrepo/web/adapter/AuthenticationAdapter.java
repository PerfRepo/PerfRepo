package org.perfrepo.web.adapter;

import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;

/**
 * The adapter for user authentication, creating and removing authentication tokens.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface AuthenticationAdapter {

    /**
     * Create authentication token for the user if the credentials are correct.
     *
     * @param credentials User credentials.
     * @return Object {@link AuthenticationResult}, it contains user data, authentication token and token expiration date
     * @throws org.perfrepo.web.adapter.exceptions.UnauthorizedException If the user does not exist or the password is wrong.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     *
     * @return The authentication object, token, user info and expiration date.
     */
    AuthenticationResult login(LoginCredentialParams credentials);

    /**
     * Invalidate user's authenticate token.
     *
     * @param token Authentication token that will be invalidated.
     *
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void logout(String token);

}
