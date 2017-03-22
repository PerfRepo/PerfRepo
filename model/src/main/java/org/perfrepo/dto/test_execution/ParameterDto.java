package org.perfrepo.dto.test_execution;

/**
 * Represents a execution parameter of a test execution.
 * (for example: environment info, commit hash, version info...)
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ParameterDto {

    private String name;

    private String value;

    private boolean favourite;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterDto)) return false;

        ParameterDto that = (ParameterDto) o;

        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
