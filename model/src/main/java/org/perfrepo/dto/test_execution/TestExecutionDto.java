package org.perfrepo.dto.test_execution;

import org.perfrepo.dto.group.GroupDto;

import java.util.Date;
import java.util.Set;

/**
 * Data transfer object for {@link org.perfrepo.model.TestExecution} entity that represents a execution of the test.
 * Not completed! TODO
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionDto {

    private Long id;

    private String name;

    private Long testId;

    private Set<String> tags;

    private String comment;

    private GroupDto group;

    private Date started;

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

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
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

    public GroupDto getGroup() {
        return group;
    }

    public void setGroup(GroupDto group) {
        this.group = group;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
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
