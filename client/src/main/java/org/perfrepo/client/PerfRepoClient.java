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

import java.util.Collection;
import org.apache.log4j.Logger;
import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.dto.user.UserDto;
import org.perfrepo.dto.util.authentication.AuthenticationResult;

/**
 * Performance Repository REST API Client.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
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

    public PerfRepoClient(String username, String password, String repositoryUrl) {
        this.username = username;
        this.password = password;
        this.repositoryUrl = repositoryUrl;
    }

    public void login() {

    }

    public void logout() {

    }

    public Collection<UserDto> getAllUsers() {

    }

    public Collection<GroupDto> getAllGroups() {

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
}
