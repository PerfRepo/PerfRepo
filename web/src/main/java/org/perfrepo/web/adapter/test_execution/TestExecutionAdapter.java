package org.perfrepo.web.adapter.test_execution;

import org.perfrepo.dto.SearchResult;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchParams;


import java.util.List;

/**
 * Service adapter for {@link TestExecutionDto} object. Adapter supports CRUD and another operations over this object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface TestExecutionAdapter {

    TestExecutionDto getTestExecutionById(Long id);

    TestExecutionDto createTestExecution(TestExecutionDto testExecution);

    TestExecutionDto updateTestExecution(TestExecutionDto testExecution);

    void deleteTestExecution(Long id);

    List<TestExecutionDto> getAllTestExecutions();

    SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchParams searchParams);

    // TODO

}
