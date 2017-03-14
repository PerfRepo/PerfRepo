package org.perfrepo.dto.report;

import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;

/**
 * Data transfer object for report access permission.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class PermissionDto {

    private AccessLevel level;

    private AccessType type;

    /** Property is used for access level {@link AccessLevel#GROUP} */
    private Long groupId;

    /** Property is used for access level {@link AccessLevel#USER} */
    private Long userId;

    /** Only for view. */
    private String groupName;

    /** Only for view. */
    private String userFullName;

    public AccessLevel getLevel() {
        return level;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    public AccessType getType() {
        return type;
    }

    public void setType(AccessType type) {
        this.type = type;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}