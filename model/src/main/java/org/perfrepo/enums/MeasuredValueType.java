package org.perfrepo.enums;

/**
 * Represents a type of test execution values of specific metric.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public enum MeasuredValueType {
    /**
     * The test execution contains only one measured value of the metric.
     */
    SINGLE_VALUE,

    /**
     * The test execution contains more than one measured value of the metric.
     */
    MULTI_VALUE,

    /**
     * The test execution contains more than one measured value of the metric,
     * but parameters of the value are invalid or missing.
     */
    INVALID_VALUE
}