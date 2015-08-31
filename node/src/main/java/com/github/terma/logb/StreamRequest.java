package com.github.terma.logb;

import java.io.Serializable;
import java.util.HashSet;

public class StreamRequest implements Serializable {

    public HashSet<String> tags;
    public long before;
    public int limit;
    public String query;

}
