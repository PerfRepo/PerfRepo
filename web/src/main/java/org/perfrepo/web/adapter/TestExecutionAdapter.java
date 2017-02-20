package org.perfrepo.web.adapter;

import org.perfrepo.dto.test_execution.*;
import org.perfrepo.dto.util.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Service adapter for {@link org.perfrepo.dto.test_execution.TestExecutionDto} object.
 * Adapter supports CRUD and another operations over this object.
 * TODO adapter is not complete
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface TestExecutionAdapter {

    TestExecutionDto getTestExecution(Long id);

    TestExecutionDto createTestExecution(TestExecutionDto testExecution);

    TestExecutionDto updateTestExecution(TestExecutionDto testExecution);

    TestExecutionDto updateTestExecutionParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters);

    void removeTestExecution(Long id);

    List<TestExecutionDto> getAllTestExecutions();

    SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams);

    AttachmentDto getTestExecutionAttachment(Long attachmentId);

    TestExecutionDto addExecutionValues(Long testExecutionId, Long metricId, List<ValueDto> executionValues);

    TestExecutionDto setExecutionValues(Long testExecutionId, Long metricId, List<ValueDto> executionValues);
}
