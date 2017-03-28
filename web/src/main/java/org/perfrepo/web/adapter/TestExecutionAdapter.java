package org.perfrepo.web.adapter;

import org.perfrepo.dto.test_execution.*;
import org.perfrepo.dto.test_execution.mass_operation.ParameterMassOperationDto;
import org.perfrepo.dto.test_execution.mass_operation.TagMassOperationDto;
import org.perfrepo.dto.util.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Service adapter for test execution. Adapter provides operations for {@link TestExecutionDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface TestExecutionAdapter {
    /**
     * Return {@link TestExecutionDto} object by its id.
     *
     * @param id The test execution identifier.
     *
     * @return The found {@link TestExecutionDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestExecutionDto getTestExecution(Long id);

    /**
     * Create new {@link TestExecutionDto} object.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Execution name</li>
     *  <li><strong>tags</strong> - Tags</li>
     *  <li><strong>started</strong> - Started date</li>
     *  <li><strong>comment</strong> - Execution description</li>
     * <ul/>
     *
     * @param testExecution Parameters of the test execution that will be created.
     *
     * @return The created {@link TestExecutionDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestExecutionDto createTestExecution(TestExecutionDto testExecution);

    /**
     * Update the {@link TestExecutionDto} object.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Execution name</li>
     *  <li><strong>tags</strong> - Tags</li>
     *  <li><strong>started</strong> - Started date</li>
     *  <li><strong>comment</strong> - Execution description</li>
     * <ul/>
     *
     * @param testExecution Parameters of the test execution that will be updated.
     *
     * @return The updated {@link TestExecutionDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestExecutionDto updateTestExecution(TestExecutionDto testExecution);

    /**
     * Set execution parameters to the test execution. The old parameters are removed.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Execution parameter name - one validation error for all parameters</li>
     *  <li><strong>value</strong> - Execution parameter value - one validation error for all parameters</li>
     * <ul/>
     *
     * @param testExecutionId The test execution identifier.
     * @param testExecutionParameters The sets of new execution parameters.
     *
     * @return The updated {@link TestExecutionDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestExecutionDto setTestExecutionParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters);

    /**
     * Add measured values to the test execution. The old values are preserved.
     *
     * @param testExecutionId The test execution identifier.
     * @param valuesGroup The sets of new execution measured values.
     *
     * @return The updated {@link TestExecutionDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestExecutionDto addExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroup);

    /**
     * Set measured values to the test execution. The old values are removed.
     *
     * @param testExecutionId The test execution identifier.
     * @param valuesGroup The sets of new execution measured values.
     *
     * @return The updated {@link TestExecutionDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.ValidationException If the input parameters are not valid.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    TestExecutionDto setExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroup);

    /**
     * Remove the {@link TestExecutionDto} object.
     *
     * @param id The test execution identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    void removeTestExecution(Long id);

    /**
     * Return all test executions.
     *
     * @return List of all test executions.
     */
    List<TestExecutionDto> getAllTestExecutions();

    /**
     * Return all {@link TestExecutionDto} test executions that satisfy search conditions.
     *
     * @param searchParams The test execution search criteria params.
     *
     * @return List of {@link TestExecutionDto} test executions.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams);

    /**
     * Return saved test execution search params.
     *
     * @return Search criteria params.
     */
    TestExecutionSearchCriteria getSearchCriteria();

    /**
     * Return the attachment of the test execution.
     *
     * @param attachmentId The attachment identifier.
     *
     * @return The found {@link AttachmentDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.AdapterException If anything bad happened.
     */
    AttachmentDto getTestExecutionAttachment(Long attachmentId, String hash);

    /**
     * Mass operation, add tags to test executions.
     *
     * @param massOperation Tags and test executions.
     */
    void addTags(TagMassOperationDto massOperation);

    /**
     * Mass operation, remove tags from test executions.
     *
     * @param massOperation Tags and test executions.
     */
    void removeTags(TagMassOperationDto massOperation);

    /**
     * Mass operation, add a parameter to test executions.
     *
     * @param massOperation Parameter and test executions.
     */
    void addParameter(ParameterMassOperationDto massOperation);

    /**
     * Mass operation, remove a parameter from test executions.
     *
     * @param massOperation Parameter and test executions.
     */
    void removeParameter(ParameterMassOperationDto massOperation);

    /**
     * Mass operation, remove test executions.
     *
     * @param testExecutionIds Test executions IDs.
     */
    void removeTestExecutions(Set<Long> testExecutionIds);
}
