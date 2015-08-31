package com.github.terma.logb;

import java.io.Serializable;
import java.util.HashSet;

public class StreamEvent implements Serializable, Comparable<StreamEvent> {

    public final String path;
    public final long timestamp;
    public final String message;
    public final HashSet<String> tags;

    public StreamEvent(final String path, final long timestamp,
                       final String message, final HashSet<String> tags) {
        this.path = path;
        this.timestamp = timestamp;
        this.message = message;
        this.tags = tags;
    }

    @Override
    public int compareTo(StreamEvent o) {
        return Long.compare(timestamp, o.timestamp);
    }

}
