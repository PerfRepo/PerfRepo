package org.perfrepo.dto.test_execution;
import org.perfrepo.dto.test.TestDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Data transfer object for {@link org.perfrepo.web.model.TestExecution} entity that represents a execution of the test.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionDto {

    private Long id;

    private String name;

    private TestDto test;

    private Set<String> tags;

    private String comment;

    private Date started;

    private Set<ParameterDto> executionParameters;

    private Set<ValuesGroupDto> executionValuesGroups;

    private List<AttachmentDto> executionAttachments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestDto getTest() {
        return test;
    }

    public void setTest(TestDto test) {
        this.test = test;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Set<ParameterDto> getExecutionParameters() {
        return executionParameters;
    }

    public void setExecutionParameters(Set<ParameterDto> executionParameters) {
        this.executionParameters = executionParameters;
    }

    public Set<ValuesGroupDto> getExecutionValuesGroups() {
        return executionValuesGroups;
    }

    public void setExecutionValuesGroups(Set<ValuesGroupDto> executionValuesGroups) {
        this.executionValuesGroups = executionValuesGroups;
    }

    public List<AttachmentDto> getExecutionAttachments() {
        return executionAttachments;
    }

    public void setExecutionAttachments(List<AttachmentDto> executionAttachments) {
        this.executionAttachments = executionAttachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestExecutionDto)) return false;

        TestExecutionDto that = (TestExecutionDto) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
