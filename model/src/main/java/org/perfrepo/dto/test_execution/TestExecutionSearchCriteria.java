package org.perfrepo.dto.test_execution;

import org.perfrepo.enums.OrderBy;

import java.util.Date;
import java.util.Set;

/**
 * Data transfer object that contains filter parameters for {@link TestExecutionDto} searching query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionSearchCriteria {

    private Set<Long> idsFilter;

    private Set<String> namesFilter;

    private Set<String> testNamesFilter;

    private Set<String> testUniqueIdsFilter;

    private Set<String> groupsFilter;

    private Set<String> tagQueriesFilter;

    private Set<String> parameterQueriesFilter;

    private Set<Date> startedAfterFilter;

    private Set<Date> startedBeforeFilter;

    private int limit = 10;

    private int offset = 0;

    private OrderBy orderBy = OrderBy.NAME_ASC;

    public Set<Long> getIdsFilter() {
        return idsFilter;
    }

    public void setIdsFilter(Set<Long> idsFilter) {
        this.idsFilter = idsFilter;
    }

    public Set<String> getNamesFilter() {
        return namesFilter;
    }

    public void setNamesFilter(Set<String> namesFilter) {
        this.namesFilter = namesFilter;
    }

    public Set<String> getTestNamesFilter() {
        return testNamesFilter;
    }

    public void setTestNamesFilter(Set<String> testNamesFilter) {
        this.testNamesFilter = testNamesFilter;
    }

    public Set<String> getTestUniqueIdsFilter() {
        return testUniqueIdsFilter;
    }

    public void setTestUniqueIdsFilter(Set<String> testUniqueIdsFilter) {
        this.testUniqueIdsFilter = testUniqueIdsFilter;
    }

    public Set<String> getGroupsFilter() {
        return groupsFilter;
    }

    public void setGroupsFilter(Set<String> groupsFilter) {
        this.groupsFilter = groupsFilter;
    }

    public Set<String> getTagQueriesFilter() {
        return tagQueriesFilter;
    }

    public void setTagQueriesFilter(Set<String> tagQueriesFilter) {
        this.tagQueriesFilter = tagQueriesFilter;
    }

    public Set<String> getParameterQueriesFilter() {
        return parameterQueriesFilter;
    }

    public void setParameterQueriesFilter(Set<String> parameterQueriesFilter) {
        this.parameterQueriesFilter = parameterQueriesFilter;
    }

    public Set<Date> getStartedAfterFilter() {
        return startedAfterFilter;
    }

    public void setStartedAfterFilter(Set<Date> startedAfterFilter) {
        this.startedAfterFilter = startedAfterFilter;
    }

    public Set<Date> getStartedBeforeFilter() {
        return startedBeforeFilter;
    }

    public void setStartedBeforeFilter(Set<Date> startedBeforeFilter) {
        this.startedBeforeFilter = startedBeforeFilter;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }
}
