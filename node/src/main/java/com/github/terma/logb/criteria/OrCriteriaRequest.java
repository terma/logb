package com.github.terma.logb.criteria;

import java.util.Objects;

public class OrCriteriaRequest implements CriteriaRequest {

    public CriteriaRequest left;
    public CriteriaRequest right;

    public OrCriteriaRequest(CriteriaRequest left, CriteriaRequest right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrCriteriaRequest that = (OrCriteriaRequest) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean accept(String string) {
        return left.accept(string) || right.accept(string);
    }
}
