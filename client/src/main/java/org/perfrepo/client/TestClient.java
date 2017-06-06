package org.perfrepo.client;

import java.util.List;
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

    public TestDto getById(Long id) {
        return null;
    }

    public TestDto getByUid(String uid) {
        return null;
    }

    public List<TestDto> search(TestSearchCriteria searchParams) {
        return null;
    }

    public void create(TestDto testDto) {

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
        return false;
    }

    public void addSubscriber(Long testId) {

    }

    public void removeSubscriber(Long testId) {

    }

    public List<AlertDto> getAllAlertsForTest(Long testId) {
        return null;
    }

    public List<TestDto> getAllTests() {
        return null;
    }
}
