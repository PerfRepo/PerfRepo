package org.perfrepo.dto.test_execution.mass_operation;

import java.util.Set;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TagMassOperationDto {

    private Set<String> tags;

    private Set<Long> testExecutionIds;

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<Long> getTestExecutionIds() {
        return testExecutionIds;
    }

    public void setTestExecutionIds(Set<Long> testExecutionIds) {
        this.testExecutionIds = testExecutionIds;
    }
}