/*
 * Copyright 2017 xkrajcik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.perfrepo.dto.test_execution.ParameterDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.test_execution.ValueDto;

/**
 *
 * @author xkrajcik
 */
public class TestExecutionClient {

    private PerfRepoClient parentClient;

    public TestExecutionClient(PerfRepoClient parentClient) {
        this.parentClient = parentClient;
    }

    public TestExecutionDto getTestExecutionById(Long testExecutionId) {

    }

    public Collection<TestExecutionDto> getAllTestExecutions() {

    }

    public Collection<TestExecutionDto> search(TestExecutionSearchCriteria searchParams) {

    }

    public String createTestExecution(TestExecutionDto testExecutionDto) {

    }

    public void updateTestExecution(TestExecutionDto testExecutionDto) {

    }

    public void deleteTestExecution(TestExecutionDto testExecutionDto) {

    }

    public void setParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters) {

    }

    public void addExecutionValues(Long testExecutionId, Long metricId, List<ValueDto> values) {

    }

    public void setExecutionValues(Long testExecutionId, Long metricId, List<ValueDto> values) {

    }

    public String downloadAttachment(Long attachmentId, String hash) {

    }

    //RestToBeChanged
    public void uploadAttachment() {

    }
}
