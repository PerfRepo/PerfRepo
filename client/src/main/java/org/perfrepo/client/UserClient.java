package org.perfrepo.client;

import org.perfrepo.dto.user.UserDto;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

/**
 * Created by UCTOVNIK on 10.04.2017.
 */
public class UserClient {

    private PerfRepoClient parentClient;

    public Collection<UserDto> getAll() {
        URL url = null;
        try {
            url = new URL(parentClient.getRepositoryUrl() + "api/json/user");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
