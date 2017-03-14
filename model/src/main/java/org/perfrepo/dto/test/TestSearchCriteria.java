package org.perfrepo.dto.test;

import org.perfrepo.enums.OrderBy;

import java.util.Set;

/**
 * Data transfer object that contains filter parameters for {@link TestDto} searching query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestSearchCriteria {

    /** Used in reports to search test in select component */
    private String generalSearch;

    private Set<String> nameFilters;

    private Set<String> uidFilters;

    private Set<String> groupFilters;

    private int limit = 5;

    private int offset = 0;

    private OrderBy orderBy;

    public String getGeneralSearch() {
        return generalSearch;
    }

    public void setGeneralSearch(String generalSearch) {
        this.generalSearch = generalSearch;
    }

    /**
     * Returns set of filters for test {@link TestDto#getName() name}.
     *
     * @return {@link TestDto#getName() Name} filters.
     */
    public Set<String> getNameFilters() {
        return nameFilters;
    }

    /**
     * Sets set of filters for test {@link TestDto#getName() name}.
     *
     * @param nameFilters {@link TestDto#getName() Name} filters.
     */
    public void setNameFilters(Set<String> nameFilters) {
        this.nameFilters = nameFilters;
    }

    /**
     * Returns set of filters for test {@link TestDto#getUid() uid}.
     *
     * @return {@link TestDto#getUid() Uid} filters.
     */
    public Set<String> getUidFilters() {
        return uidFilters;
    }

    /**
     * Sets set of filters for test {@link TestDto#getUid() uid}.
     *
     * @param uidFilters {@link TestDto#getUid() Uid} filters.
     */
    public void setUidFilters(Set<String> uidFilters) {
        this.uidFilters = uidFilters;
    }

    /**
     * Returns set of filters for test {@link TestDto#getGroup() group}.
     *
     * @return  {@link TestDto#getGroup() Group} filters.
     */
    public Set<String> getGroupFilters() {
        return groupFilters;
    }

    /**
     * Sets set of filters for test {@link TestDto#getGroup()}  group}.
     *
     * @param groupFilters {@link TestDto#getGroup() Group} filters.
     */
    public void setGroupFilters(Set<String> groupFilters) {
        this.groupFilters = groupFilters;
    }

    /**
     * Returns the limit count of search result. It is count of records for one page.
     *
     * @return Limit count of records.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit count of records for search result. It is count of records for one page.
     *
     * @param limit Limit count of records.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the offset of the first record which is returned. Offset starts with index 0.
     *
     * @return Offset of the first record.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset of the first record which is returned.  Offset starts with index 0.
     *
     * @param offset Offset of the first record.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Returns name of {@link TestDto} parameter which specifies the rule of ordering.
     *
     * @return Order by parameter.
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Sets name of {@link TestDto} parameter which specifies the rule of ordering.
     *
     * @param orderBy Order by parameter.
     */
    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }
}
