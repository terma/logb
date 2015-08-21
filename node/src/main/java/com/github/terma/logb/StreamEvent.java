package com.github.terma.logb;

import java.io.Serializable;
import java.util.HashSet;

public class StreamEvent implements Serializable {

    public final String path;
    public final long timestamp;
    public final String message;
    public final HashSet<String> tags;

    public StreamEvent(String path, long timestamp, String message, HashSet<String> tags) {
        this.path = path;
        this.timestamp = timestamp;
        this.message = message;
        this.tags = tags;
    }

}
