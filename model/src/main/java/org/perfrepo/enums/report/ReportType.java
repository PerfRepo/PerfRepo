package org.perfrepo.enums.report;

/**
 * Represents type of report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public enum ReportType {

    /**
     * Show results for specific metrics in history
     */
    METRIC_HISTORY,

    /**
     * Compare multiple sets of test executions against each other, show differences etc.
     */
    TABLE_COMPARISON,

    /**
     * Compute boxplots for test executions and compare them across different test runs.
     */
    BOX_PLOT
}