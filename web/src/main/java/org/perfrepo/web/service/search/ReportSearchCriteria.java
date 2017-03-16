package org.perfrepo.web.service.search;

import org.perfrepo.enums.OrderBy;
import org.perfrepo.enums.ReportFilter;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ReportSearchCriteria {

    private String name;
    private OrderBy orderBy = OrderBy.NAME_ASC;
    private ReportFilter filter = ReportFilter.TEAM;
    private Integer limitFrom;
    private Integer limitHowMany;

    public ReportFilter getFilter() {
        return filter;
    }

    public void setFilter(ReportFilter filter) {
        this.filter = filter;
    }

    public Integer getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom(Integer limitFrom) {
        this.limitFrom = limitFrom;
    }

    public Integer getLimitHowMany() {
        return limitHowMany;
    }

    public void setLimitHowMany(Integer limitHowMany) {
        this.limitHowMany = limitHowMany;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }
}
