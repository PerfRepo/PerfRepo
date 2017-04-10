package org.perfrepo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.perfrepo.client.exceptions.ConnectionError;
import org.perfrepo.client.exceptions.LoginException;
import org.perfrepo.dto.alert.AlertDto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *
 * @author xkrajcik
 */
public class AlertClient {

    private PerfRepoClient parentClient;

    public AlertClient(PerfRepoClient parentClient) {
        this.parentClient = parentClient;
    }

    public AlertDto getById(Long alertId) throws Exception{
        AlertDto result = null;
        URL url = new URL(parentClient.getRepositoryUrl() + "api/json/alert/" + alertId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + parentClient.getToken().getToken());

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(response, AlertDto.class);
        } else {
            throw new ConnectionError();
        }

        return result;
    }

    public void create(AlertDto alertDto) throws Exception {
        if (alertDto == null) {
            throw new NullPointerException("AlertDto is null.");
        }

        URL url = new URL(parentClient.getRepositoryUrl() + "api/json/alert/create");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        ObjectMapper objectMapper = new ObjectMapper();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authentication", "Bearer " + parentClient.getToken().getToken());
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

        wr.writeBytes(objectMapper.writeValueAsString(alertDto));
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();

        if (responseCode == 201) {
        } else {
            throw new LoginException();
        }
    }

    public void update(AlertDto alertDto) {

    }

    public void delete(Long AlertId) {

    }

}
