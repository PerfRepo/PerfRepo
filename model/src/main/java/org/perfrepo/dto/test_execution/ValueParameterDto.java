package org.perfrepo.dto.test_execution;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueParameterDto {

    private String name;

    private double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueParameterDto)) return false;

        ValueParameterDto that = (ValueParameterDto) o;

        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}