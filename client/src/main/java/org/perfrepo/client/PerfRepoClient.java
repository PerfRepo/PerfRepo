/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.perfrepo.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.perfrepo.client.exceptions.LoginException;
import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;

/**
 * Performance Repository REST API Client.
 *
 * @author Adam Krajcik (adam.krajcik@gmail.com)
 *
 */
public class PerfRepoClient {

    private static final Logger log = Logger.getLogger(PerfRepoClient.class);

    private String username;
    private String password;
    private String repositoryUrl;
    private AuthenticationResult token;

    private TestClient testClient;
    private TestExecutionClient testExecutionClient;
    private ReportClient reportClient;
    private AlertClient alertClient;
    private UserClient userClient;
    private GroupClient groupClient;

    public PerfRepoClient(String username, String password, String repositoryUrl) {
        this.username = username;
        this.password = password;
        this.repositoryUrl = repositoryUrl;


    }

    public void login() throws Exception {
        URL url = new URL(repositoryUrl + "api/json/authorize");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        LoginCredentialParams credentials = new LoginCredentialParams();
        credentials.setUsername(username);
        credentials.setPassword(password);

        ObjectMapper objectMapper = new ObjectMapper();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

        wr.writeBytes(objectMapper.writeValueAsString(credentials));
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            token = objectMapper.readValue(response, AuthenticationResult.class);
            in.close();

        } else {
            throw new LoginException();
        }


    }

    public void logout() {

    }

    public TestClient getTestClient() {
        return testClient;
    }

    public TestExecutionClient getTestExecutionClient() {
        return testExecutionClient;
    }

    public ReportClient getReportClient() {
        return reportClient;
    }

    public AlertClient getAlertClient() {
        return alertClient;
    }

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

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public UserClient getUserClient() { return userClient; }

    public GroupClient getGroupClient() { return groupClient; }

    public TestClient tests() { return testClient; }

    public TestExecutionClient testExecutions() { return testExecutionClient; }

    public ReportClient reports() { return reportClient; }

    public AlertClient alerts() { return alertClient; }

    public UserClient users() { return userClient; }

    public GroupClient groups() { return groupClient; }
}
