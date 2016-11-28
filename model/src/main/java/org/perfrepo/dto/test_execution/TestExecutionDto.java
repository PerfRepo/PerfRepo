package org.perfrepo.dto.test_execution;

import java.security.acl.Group;
import java.util.Date;
import java.util.List;

/**
 * Data transfer object for {@link org.perfrepo.model.TestExecution} entity that represents a execution of the test.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionDto {

    private Long id;

    private String name;

    private Long testId;

    private List<String> tags;

    private String comment;

    private Group group;

    private Date started;

    //TODO

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
