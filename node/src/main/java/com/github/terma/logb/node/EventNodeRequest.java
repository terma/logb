package com.github.terma.logb.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventNodeRequest implements Serializable {

    public List<EventPath> paths = new ArrayList<>();
    public ArrayList<String> tags;
    public long from;
    public long to;
    public String pattern;

}
