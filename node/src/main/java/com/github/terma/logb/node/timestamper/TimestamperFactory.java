package com.github.terma.logb.node.timestamper;

import com.github.terma.logb.node.EventStreamPath;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TimestamperFactory {

    private final static Logger LOGGER = Logger.getLogger(TimestamperFactory.class.getName());

    public static Timestamper get(final EventStreamPath path) {
        try {
            return new RealTimestamper(path.timestampFormat, path.timestampPattern);
        } catch (NullPointerException | IllegalArgumentException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            return new ZeroTimestamper();
        }
    }

}

