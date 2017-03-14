package org.perfrepo.enums.report;

/**
 * The table comparison report contains selected test executions for comparison. It is possible to select
 * each test execution by ID, tag query or parameter query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public enum ComparisonItemSelector {
    /**
     * Test execution is selected by its ID.
     */
    TEST_EXECUTION_ID,

    /**
     * Test execution is selected by tag query.
     */
    TAG_QUERY,

    /**
     * Test execution is selected by parameter query.
     */
    PARAMETER_QUERY
}