package com.github.terma.logb.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CollectionUtils {

    public static <T> ArrayList<T> al(T... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

    public static <T> HashSet<T> hs(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

}
