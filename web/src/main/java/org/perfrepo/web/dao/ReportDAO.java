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

import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.enums.ReportFilter;
import org.perfrepo.web.model.report.Permission;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.search.ReportSearchCriteria;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DAO layer for report entity.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ReportDAO extends DAO<Report, Long> {

    @Inject
    private GroupDAO groupDAO;

    /**
     * Main search method for tests. All criteria are passed via transfer object.
     *
     * @param search
     * @return
     */
    public SearchResultWrapper<Report> searchReports(ReportSearchCriteria search, User user) {
        CriteriaQuery<Report> criteria = createCriteria();

        int lastQueryResultsCount = processSearchCountQuery(search, user);

        Root<Report> root = criteria.from(Report.class);
        criteria.select(root);
        List<Predicate> predicates = createSearchPredicates(search, user, root);

        if (!predicates.isEmpty()) {
            criteria.where(predicates.toArray(new Predicate[0]));
        }

        setOrderBy(criteria, search.getOrderBy(), root);

        TypedQuery<Report> query = query(criteria);
        int firstResult = search.getLimitFrom() == null ? 0 : search.getLimitFrom();
        query.setFirstResult(firstResult);
        if (search.getLimitHowMany() != null) {
            query.setMaxResults(search.getLimitHowMany());
        }

        return new SearchResultWrapper<>(query.getResultList(), lastQueryResultsCount);
    }

    /**
     * Helper method. Retrieves total number of found reports according to specified
     * search criteria.
     *
     * @param search
     * @return
     */
    private int processSearchCountQuery(ReportSearchCriteria search, User user) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        Root<Report> root = countQuery.from(Report.class);
        countQuery.select(cb.countDistinct(root));
        List<Predicate> predicates = createSearchPredicates(search, user, root);

        if (!predicates.isEmpty()) {
            countQuery.where(predicates.toArray(new Predicate[0]));
        }

        Long count = query(countQuery).getSingleResult();
        return count.intValue();
    }

    /**
     * Helper method. Since the same predicates are put to search query in count query and actual retrieval query,
     * construction of these predicate is extracted into this method to avoid code duplication.
     *
     * @param search
     * @param user
     * @param root
     * @return
     */
    private List<Predicate> createSearchPredicates(ReportSearchCriteria search, User user, Root root) {
        CriteriaBuilder cb = criteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        if (search.getName() != null && !search.getName().isEmpty()) {
            if (search.getName().contains("*")) {
                String pattern = search.getName().replace("*", "%").toLowerCase();
                predicates.add(cb.like(cb.lower(root.<String>get("name")), pattern));
            } else {
                predicates.add(cb.equal(cb.lower(root.<String>get("name")), search.getName()));
            }
        }

        List<Predicate> permissionPredicates = createPermissionPredicates(search.getFilter(), user, root);
        predicates.addAll(permissionPredicates);

        return predicates;
    }

    /**
     * TODO: document this
     *
     * @param filter
     * @param user
     * @param root
     * @return
     */
    private List<Predicate> createPermissionPredicates(ReportFilter filter, User user, Root root) {
        CriteriaBuilder cb = criteriaBuilder();
        List<Predicate> permissionPredicates = new ArrayList<>();

        if (filter == ReportFilter.MY) {
            permissionPredicates.add(cb.equal(root.get("user"), user));
        } else {
            Join<Report, Permission> permissionJoin = root.join("permissions");
            Set<Group> userGroups = groupDAO.getUserGroups(user);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(permissionJoin.get("group").in(userGroups));
            predicates.add(cb.equal(permissionJoin.get("user"), user));

            if (filter == ReportFilter.ALL) {
                predicates.add(cb.equal(permissionJoin.get("level"), AccessLevel.PUBLIC));
            }

            permissionPredicates.add(cb.or(predicates.toArray(new Predicate[0])));
        }

        return permissionPredicates;
    }

    /**
     * Sets ordering to search query depending on specified parameters.
     *
     * @param criteria
     * @param orderBy
     * @param root
     */
    private void setOrderBy(CriteriaQuery criteria, OrderBy orderBy, Root root) {
        CriteriaBuilder cb = criteriaBuilder();

        Order order;
        switch (orderBy) {
            case NAME_ASC:
                order = cb.asc(root.get("name"));
                break;
            case NAME_DESC:
                order = cb.desc(root.get("name"));
                break;
            case TYPE_ASC:
                order = cb.asc(root.get("type"));
                break;
            case TYPE_DESC:
                order = cb.desc(root.get("type"));
                break;
            default:
                order = cb.asc(root.get("name"));
        }

        criteria.orderBy(order);
    }

}