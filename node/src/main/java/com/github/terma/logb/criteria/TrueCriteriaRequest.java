package com.github.terma.logb.criteria;

public class TrueCriteriaRequest implements CriteriaRequest {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean accept(String string) {
        return true;
    }

}
