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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RealTimestamper implements Timestamper {

    private final static Logger LOGGER = Logger.getLogger(RealTimestamper.class.getName());

    private final Pattern pattern;
    private final DateTimeFormatter formatter;

    public RealTimestamper(String timestampFormat, String pattern) {
        this.pattern = Pattern.compile(pattern);
        this.formatter = DateTimeFormat.forPattern(timestampFormat).withZoneUTC();
    }

    @Override
    public long get(String line) {
        try {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                final String timestampString = matcher.group();
                return formatter.parseMillis(timestampString);
            } else {
                LOGGER.log(Level.INFO, "Can't find timestamp by " + pattern.pattern() + " in " + line);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            // to skip
        }
        return 0;
    }
}
