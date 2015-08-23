package com.github.terma.logb.node.timestamper;

import com.github.terma.logb.node.EventPath;

public class TimestamperFactory {

    public static Timestamper get(final EventPath path) {
        try {
            return new RealTimestamper(path.timestampFormat, path.timestampPattern);
        } catch (NullPointerException | IllegalArgumentException e) {
            return new ZeroTimestamper();
        }
    }

}

