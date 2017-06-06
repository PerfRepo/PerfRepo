package org.perfrepo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.perfrepo.client.exceptions.ConnectionError;
import org.perfrepo.dto.user.UserDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by UCTOVNIK on 10.04.2017.
 */
public class UserClient {

    private PerfRepoClient parentClient;

    public UserClient(PerfRepoClient parentClient) {
        this.parentClient = parentClient;
    }

    public List<UserDto> getAll() throws Exception{
        List<UserDto> result = null;
        URL url = new URL(parentClient.getRepositoryUrl() + "api/json/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + parentClient.getToken().getToken());

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            result = Arrays.asList(mapper.readValue(response, UserDto[].class));
        } else {
            throw new ConnectionError();
        }

        return result;
    }
}
