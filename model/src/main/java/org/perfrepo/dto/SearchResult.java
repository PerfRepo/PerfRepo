package org.perfrepo.dto;

import java.util.List;

/**
 * Wrapper object for search query with pagination support.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class SearchResult<T> {
    private List<T> data;
    private Integer totalCount;
    private Integer currentPage;
    private Integer pageCount;
    private Integer perPage;

    public SearchResult(List<T> data, Integer totalCount, Integer limit, Integer from) {
        this.data = data;
        this.totalCount = totalCount;
        this.perPage = limit;
        this.currentPage = (from / limit) + 1;
        this.pageCount = (int) Math.ceil(totalCount / (double)limit);

    }

    public List<T> getData() {
        return data;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public Integer getPerPage() {
        return perPage;
    }
}