package org.perfrepo.dto.report.table_comparison;

import org.perfrepo.enums.report.ComparisonItemSelector;

/**
 * Data transfer object for the table comparison report. Represents one selected test execution in the comparison table.
 * This object is used for configuration of the report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ComparisonItemDto {

    private String alias;

    private boolean baseline;

    private Long executionId;

    private Long testId;

    private String tagQuery;

    private String parameterQuery;

    private ComparisonItemSelector selector;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isBaseline() {
        return baseline;
    }

    public void setBaseline(boolean baseline) {
        this.baseline = baseline;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTagQuery() {
        return tagQuery;
    }

    public void setTagQuery(String tagQuery) {
        this.tagQuery = tagQuery;
    }

    public String getParameterQuery() {
        return parameterQuery;
    }

    public void setParameterQuery(String parameterQuery) {
        this.parameterQuery = parameterQuery;
    }

    public ComparisonItemSelector getSelector() {
        return selector;
    }

    public void setSelector(ComparisonItemSelector selector) {
        this.selector = selector;
    }
}