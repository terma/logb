package com.github.terma.logb.criteria;

import java.util.Objects;

public class PlainCriteriaRequest implements CriteriaRequest {

    public String value;

    public PlainCriteriaRequest(String value) {
        this.value = value.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainCriteriaRequest that = (PlainCriteriaRequest) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PlainCriteriaRequest{" + "value='" + value + '\'' + '}';
    }

    @Override
    public boolean accept(String string) {
        return string.toLowerCase().contains(value.toLowerCase());
    }

}
