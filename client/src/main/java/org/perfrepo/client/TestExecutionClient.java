package org.perfrepo.client;

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

    public TestExecutionDto getById(Long testExecutionId) {
        return null;
    }

    public List<TestExecutionDto> getAll() {
        return null;
    }

    public List<TestExecutionDto> search(TestExecutionSearchCriteria searchParams) {
        return null;
    }

    public void create(TestExecutionDto testExecutionDto) {

    }

    public void update(TestExecutionDto testExecutionDto) {

    }

    public void delete(TestExecutionDto testExecutionDto) {

    }

    public void setParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters) {

    }

    public void addExecutionValues(Long testExecutionId, Long metricId, List<ValueDto> values) {

    }

    public void setExecutionValues(Long testExecutionId, Long metricId, List<ValueDto> values) {

    }

    public String downloadAttachment(Long attachmentId, String hash) {
        return null;
    }

    //RestToBeChanged
    public void uploadAttachment() {

    }
}
