package org.perfrepo.enums.report;

/**
 * Represents cell style of the table comparison table.
 * If the measured value of metric is better than baseline value (more than threshold value), the style will be good.
 * If the measured value of metric is worse than baseline value (more than threshold value), the style will be bad.
 * Else the style is neutral.
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public enum CellStyle {
    /**
     * The metric value is bad against the baseline value (red background)
     */
    BAD,

    /**
     * The metric value is good against the baseline value (green background)
     */
    GOOD,

    /**
     * The metric value is almost the same as the baseline value (neutral background)
     */
    NEUTRAL
}