
package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.test_execution.*;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.adapter.TestExecutionAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;
import org.perfrepo.web.model.TestExecution;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Temporary implementation of {@link TestExecutionAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionAdapterDummyImpl implements TestExecutionAdapter {

    @Inject
    private Storage storage;

    @Override
    public TestExecutionDto getTestExecution(Long id) {
        TestExecutionDto testExecution = storage.testExecution().getById(id);

        if (testExecution == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        return testExecution;
    }

    @Override
    public TestExecutionDto createTestExecution(TestExecutionDto testExecution) {
        validate(testExecution);

        testExecution.setTest(storage.test().getById(testExecution.getTest().getId()));

        // it is not possible to set it, only in test execution detail
        testExecution.setExecutionParameters(new LinkedHashSet<>());
        testExecution.setExecutionValuesGroups(new LinkedHashSet<>());

        return storage.testExecution().create(testExecution);
    }

    @Override
    public TestExecutionDto updateTestExecution(TestExecutionDto testExecution) {
        if (testExecution == null) {
            throw new BadRequestException("Bad input data.");
        }

        if (testExecution.getId() == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        TestExecutionDto originExecutionTest = storage.testExecution().getById(testExecution.getId());

        if (originExecutionTest == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        validate(testExecution);

        testExecution.setExecutionParameters(originExecutionTest.getExecutionParameters());
        testExecution.setExecutionValuesGroups(originExecutionTest.getExecutionValuesGroups());

        return storage.testExecution().update(testExecution);
    }

    @Override
    public TestExecutionDto setTestExecutionParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters) {
        TestExecutionDto testExecution = storage.testExecution().getById(testExecutionId);

        if (testExecution == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        validateParameters(testExecutionParameters);

        testExecution.setExecutionParameters(testExecutionParameters);
        return testExecution;
    }

    @Override
    public void removeTestExecution(Long id) {
        TestExecutionDto testExecution = storage.testExecution().getById(id);

        if (testExecution == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        boolean removed = storage.testExecution().delete(testExecution.getId());

        if (!removed) {
            throw new AdapterException("You can not delete test execution.");
        }
    }

    @Override
    public List<TestExecutionDto> getAllTestExecutions() {
        return storage.testExecution().getAll();
    }

    @Override
    public SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams) {
        // delay...
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return storage.testExecution().search(searchParams);
    }

    @Override
    public TestExecutionSearchCriteria getSearchCriteria() {
        return storage.testExecution().getSearchCriteria();
    }

    @Override
    public AttachmentDto getTestExecutionAttachment(Long attachmentId, String hash) {
        AttachmentDto a = storage.attachment().getById(attachmentId);

        return a;
    }

    @Override
    public TestExecutionDto addExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroup) {
        return updateExecutionValues(testExecutionId, valuesGroup, true);
    }

    @Override
    public TestExecutionDto setExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroup) {
        return updateExecutionValues(testExecutionId, valuesGroup, false);
    }

    private void validate(TestExecutionDto testExecution) {
        ValidationErrors validation = new ValidationErrors();

        // test name
        if (testExecution.getName() == null) {
            validation.addFieldError("name", "Test execution name is a required field");
        } else if (testExecution.getName().trim().length() < 3) {
            validation.addFieldError("name", "Test execution name must be at least three characters.");
        }

        if (testExecution.getTags() == null) {
            validation.addFieldError("tags", "Test execution tags is a required field");
        } else if (testExecution.getTags().isEmpty()) {
            validation.addFieldError("tags", "Test execution tags is a required field");
        }

        // test description
        if (testExecution.getComment() != null && testExecution.getComment().length() > 500) {
            validation.addFieldError("comment", "Test execution description must not be more than 500 characters.");
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Test execution contains validation errors, please fix it.", validation);
        }
    }

    private void validateParameters(Set<ParameterDto> parameters) {
        ValidationErrors validation = new ValidationErrors();

        if (parameters != null && !parameters.isEmpty()) {

            parameters.forEach(parameter -> {
                if (parameter.getName() == null || parameter.getName().length() < 3) {
                    validation.addFieldError("name", "Parameter name must be at least three characters.");
                }

                if (parameter.getValue() == null || parameter.getValue().length() < 3) {
                    validation.addFieldError("value", "Parameter value must be at least three characters.");
                }
            });
        }

        if (validation.hasErrors()) {
            throw new ValidationException("Test execution contains validation errors, please fix it.", validation);
        }
    }

    private void validateValues(Long testExecutionId, ValuesGroupDto valuesGroup) {
        if (testExecutionId == null || storage.testExecution().getById(testExecutionId) == null) {
            throw new BadRequestException("Not existing test execution.");
        }

        if (valuesGroup == null || storage.metric().getByName(valuesGroup.getMetricName()) == null) {
            throw new BadRequestException("Not existing metric.");
        }
    }

    private TestExecutionDto updateExecutionValues(Long testExecutionId,  ValuesGroupDto valuesGroup,
                                                   boolean appendValues) {
        validateValues(testExecutionId, valuesGroup);

        TestExecutionDto testExecution = storage.testExecution().getById(testExecutionId);

        ValuesGroupDto valueGroupDto = null;

        if (testExecution.getExecutionValuesGroups() != null) {
            // find existing values group
            Optional<ValuesGroupDto> valuesGroupOptional = testExecution.getExecutionValuesGroups()
                    .stream()
                    .filter(valuesGroupDto -> valuesGroupDto.getMetricName().equals(valuesGroup.getMetricName())).findFirst();
            valueGroupDto = valuesGroupOptional.isPresent() ? valuesGroupOptional.get() : null;
        }

        if (valueGroupDto == null) {
            // values group doesn't exist
            valueGroupDto = new ValuesGroupDto();
            valueGroupDto.setMetricName(valuesGroup.getMetricName());
            if (testExecution.getExecutionValuesGroups() == null) {
                testExecution.setExecutionValuesGroups(new LinkedHashSet<>());
            }
            testExecution.getExecutionValuesGroups().add(valueGroupDto);
        }

        if (valueGroupDto.getValues() == null) {
            valueGroupDto.setValues(new ArrayList<>());
        }

        if (!appendValues) {
            valueGroupDto.getValues().clear();
        }
        // add values
        valueGroupDto.getValues().addAll(valuesGroup.getValues());

        // update parameter names
        Set<String> parameterNames = valueGroupDto.getValues()
                .stream().filter(valueObject -> valueObject != null && valueObject.getParameters() != null)
                .flatMap(valueObject -> valueObject.getParameters().stream().map(ValueParameterDto::getName))
                .collect(Collectors.toSet());
        valueGroupDto.setParameterNames(parameterNames);

        // set value type
        if (valueGroupDto.getValues() == null || valueGroupDto.getValues().size() == 0) {
            // undefined value
            valueGroupDto.setValueType(null);
        } else if (valueGroupDto.getValues().size() == 1) {
            // single value
            valueGroupDto.setValueType(MeasuredValueType.SINGLE_VALUE);
        } else {
            // multi value
            if (parameterNames == null || parameterNames.size() == 0) {
                // no parameter
                valueGroupDto.setValueType(MeasuredValueType.INVALID_VALUE);
            } else {
                // each value must have all parameters
                boolean allRight = valueGroupDto.getValues()
                        .stream()
                        .allMatch(value -> value.getParameters() != null &&
                                value.getParameters().size() == parameterNames.size());
                if (allRight) {
                    valueGroupDto.setValueType(MeasuredValueType.MULTI_VALUE);
                } else {
                    valueGroupDto.setValueType(MeasuredValueType.INVALID_VALUE);
                }
            }
        }


        if (!appendValues && (valuesGroup.getValues() == null || valueGroupDto.getValues().size() == 0)) {
            // remove empty values group
            if (testExecution.getExecutionValuesGroups() != null) {
                testExecution.getExecutionValuesGroups().removeIf(group -> group.getMetricName().equals(valuesGroup.getMetricName()));
            }
        }

        return storage.testExecution().getById(testExecutionId);
    }
}
