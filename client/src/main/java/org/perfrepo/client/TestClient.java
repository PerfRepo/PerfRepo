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
import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchCriteria;

/**
 *
 * @author xkrajcik
 */
public class TestClient {

    private PerfRepoClient parentClient;

    public TestClient(PerfRepoClient parentClient) {
        this.parentClient = parentClient;
    }

    public TestDto getTestById(Long id) {

    }

    public TestDto getTestByUid(String uid) {

    }

    public Collection<TestDto> search(TestSearchCriteria searchParams) {

    }

    public Long create(TestDto testDto) {

    }

    public void update(TestDto testDto) {

    }

    public void addMetric(MetricDto metricDto) {

    }

    public void removeMetric(Long testId, Long metricId) {

    }

    public void delete(Long testId) {

    }

    public boolean isUserSubscriber(Long testId) {

    }

    public void addSubscriber(Long testId) {

    }

    public void removeSubscriber(Long testId) {

    }

    public Collection<AlertDto> getAllAlertsForTest(Long testId) {

    }

    public Collection<TestDto> getAllTests() {

    }
}
