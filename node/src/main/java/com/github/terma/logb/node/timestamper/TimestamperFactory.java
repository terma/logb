/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

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

