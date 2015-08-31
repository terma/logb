package com.github.terma.logb.node;

import java.io.Serializable;
import java.util.ArrayList;

public class EventStreamRequest implements Serializable {

    public ArrayList<String> tags = new ArrayList<>();
    public long from;
    public long to;
    public String pattern;
    public String app;

}
