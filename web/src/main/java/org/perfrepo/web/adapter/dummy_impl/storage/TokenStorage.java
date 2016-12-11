package org.perfrepo.web.adapter.dummy_impl.storage;

import java.util.*;

/**
 * Temporary in-memory user storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TokenStorage {

    private Set<String> tokens = new HashSet<>();

    public void saveToken(String token) {
        tokens.add(token);
    }

    public boolean tokenExists(String token) {
        return tokens.contains(token);
    }

    public void removeToken(String token) {
        tokens.remove(token);
    }

    public void removeAllTokens() {
        tokens.clear();
    }
}

