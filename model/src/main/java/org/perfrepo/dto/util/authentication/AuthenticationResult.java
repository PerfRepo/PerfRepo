package org.perfrepo.dto.util.authentication;

import org.perfrepo.dto.user.UserDto;

import java.util.Date;

/**
 * Represents success authentication request.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AuthenticationResult {

    private String token;

    private UserDto user;

    private Date expiration;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
