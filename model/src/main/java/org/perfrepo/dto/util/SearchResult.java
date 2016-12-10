package org.perfrepo.dto.util;

import java.util.List;

/**
 * Wrapper object for search query with pagination support.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class SearchResult<T> {
    private List<T> data;
    private int totalCount;
    private int currentPage;
    private int pageCount;
    private int perPage;

    public SearchResult(List<T> data, int totalCount, int limit, int from) {
        this.data = data;
        this.totalCount = totalCount;
        this.perPage = limit;
        this.currentPage = (from / limit) + 1;
        this.pageCount = (int) Math.ceil(totalCount / (double) limit);

    }

    public List<T> getData() {
        return data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPerPage() {
        return perPage;
    }
}