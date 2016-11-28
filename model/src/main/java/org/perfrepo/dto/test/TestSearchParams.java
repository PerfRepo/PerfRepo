package org.perfrepo.dto.test;

import java.util.List;

/**
 * Data transfer object that contains filter parameters for {@link TestDto} searching query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestSearchParams {

    private List<String> nameFilters;

    private List<String> uidFilters;

    private List<Integer> groupIdFilters;

    private Integer limit = 5;

    private Integer offset = 0;

    private String orderBy;

    /**
     * Returns list of filters for test {@link TestDto#getName() name}.
     *
     * @return {@link TestDto#getName() Name} filters.
     */
    public List<String> getNameFilters() {
        return nameFilters;
    }

    /**
     * Sets list of filters for test {@link TestDto#getName() name}.
     *
     * @param nameFilters {@link TestDto#getName() Name} filters.
     */
    public void setNameFilters(List<String> nameFilters) {
        this.nameFilters = nameFilters;
    }

    /**
     * Returns list of filters for test {@link TestDto#getUid() uid}.
     *
     * @return {@link TestDto#getUid() Uid} filters.
     */
    public List<String> getUidFilters() {
        return uidFilters;
    }

    /**
     * Sets list of filters for test {@link TestDto#getUid() uid}.
     *
     * @param uidFilters {@link TestDto#getUid() Uid} filters.
     */
    public void setUidFilters(List<String> uidFilters) {
        this.uidFilters = uidFilters;
    }

    /**
     * Returns list of filters for test {@link TestDto#getGroup() group}.
     *
     * @return  {@link TestDto#getGroup() Group} filters. Values are group identifiers.
     */
    public List<Integer> getGroupIdFilters() {
        return groupIdFilters;
    }

    /**
     * Sets list of filters for test {@link TestDto#getGroup()}  group}.
     *
     * @param groupIdFilters {@link TestDto#getGroup() Group} filters. Values are group identifiers.
     */
    public void setGroupIdFilters(List<Integer> groupIdFilters) {
        this.groupIdFilters = groupIdFilters;
    }

    /**
     * Returns the limit count of search result. It is count of records for one page.
     *
     * @return Limit count of records.
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the limit count of records for search result. It is count of records for one page.
     *
     * @param limit Limit count of records.
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Returns the offset of the first record which is returned. Offset starts with index 0.
     *
     * @return Offset of the first record.
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the offset of the first record which is returned.  Offset starts with index 0.
     *
     * @param offset Offset of the first record.
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * Returns name of {@link TestDto} parameter which specifies the rule of ordering.
     *
     * @return Order by parameter.
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Sets name of {@link TestDto} parameter which specifies the rule of ordering.
     *
     * @param orderBy Order by parameter.
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
