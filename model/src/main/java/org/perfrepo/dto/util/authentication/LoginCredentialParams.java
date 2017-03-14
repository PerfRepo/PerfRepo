package org.perfrepo.dto.util.authentication;

/**
 * Parameters for user authentication.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class LoginCredentialParams {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
