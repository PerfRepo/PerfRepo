package org.perfrepo.web.adapter.dummy_impl.storage.initialize;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class InstanceCreator {

    private InstanceCreator() {

    }

    public static PermissionDto createPermission(AccessLevel level, AccessType type, Long groupId, Long userId,
                                            String groupName, String userFullName) {
        PermissionDto permission = new PermissionDto();
        permission.setLevel(level);
        permission.setType(type);
        permission.setGroupId(groupId);
        permission.setUserId(userId);
        permission.setGroupName(groupName);
        permission.setUserFullName(userFullName);
        return permission;
    }
}