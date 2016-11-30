/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.dao;

import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.Membership;
import org.perfrepo.model.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link org.perfrepo.model.user.Membership}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class MembershipDAO extends DAO<Membership, Long> {

    public Membership getMembership(User user, Group group) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("group", group);

        // there should be always only one
        List<Membership> memberships = findByNamedQuery(Membership.GET_BY_USER_AND_GROUP, parameters);
        if (memberships.isEmpty()) {
            return null;
        }

        return memberships.get(0);
    }

}