package com.github.terma.logb.criteria;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegexCriteriaRequest implements CriteriaRequest {

    public String value;

    public RegexCriteriaRequest(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexCriteriaRequest that = (RegexCriteriaRequest) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean accept(String string) {
        Pattern pattern = Pattern.compile(value);
        return pattern.matcher(string).find();
    }

}
