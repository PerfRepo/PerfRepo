package org.perfrepo.dto.report;

import org.perfrepo.enums.OrderBy;
import org.perfrepo.enums.report.ReportType;

import java.util.Set;

/**
 * Data transfer object that contains filter parameters for {@link ReportDto} searching query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ReportSearchCriteria {

    private Set<String> namesFilter;

    private Set<ReportType> typesFilter;

    private int limit = 10;

    private int offset = 0;

    private OrderBy orderBy;

    public Set<String> getNamesFilter() {
        return namesFilter;
    }

    public void setNamesFilter(Set<String> namesFilter) {
        this.namesFilter = namesFilter;
    }

    public Set<ReportType> getTypesFilter() {
        return typesFilter;
    }

    public void setTypesFilter(Set<ReportType> typesFilter) {
        this.typesFilter = typesFilter;
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