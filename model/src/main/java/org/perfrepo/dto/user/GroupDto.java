package org.perfrepo.dto.user;

/**
 * Data transfer object for {@link org.perfrepo.model.user.Group} entity that represents a user group.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class GroupDto {

    private Long id;

    private String name;

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
}
