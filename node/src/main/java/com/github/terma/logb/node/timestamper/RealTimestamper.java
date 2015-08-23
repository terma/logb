package com.github.terma.logb.node.timestamper;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RealTimestamper implements Timestamper {

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
            }
        } catch (IllegalArgumentException e) {
            // to skip
        }
        return 0;
    }
}
