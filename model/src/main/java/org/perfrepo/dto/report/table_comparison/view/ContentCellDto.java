package org.perfrepo.dto.report.table_comparison.view;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Data transfer object for the table comparison report. Represents abstract value cell in the comparison table.
 * This object is used for view of the report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@JsonTypeInfo (
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleContentCellDto.class, name = "SINGLE_CONTENT_CELL"),
        @JsonSubTypes.Type(value = MultiContentCellDto.class, name = "MULTI_CONTENT_CELL") })
public abstract class ContentCellDto {

    private boolean baseline;

    public boolean isBaseline() {
        return baseline;
    }

    public void setBaseline(boolean baseline) {
        this.baseline = baseline;
    }
}