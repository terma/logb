package com.github.terma.logb.node;

import com.github.terma.logb.StreamEvent;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventStreamNode {

    private final Tagger tagger;

    public EventStreamNode(Tagger tagger) {
        this.tagger = tagger;
    }

    private static long getTimestamp(EventPath path, String line) {
        long timestamp = 0;
        if (path.timestampFormat != null && path.timestampPattern != null) {
            try {
                Pattern pattern = Pattern.compile(path.timestampPattern);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String timestampString = matcher.group();
                    timestamp = DateTimeFormat.forPattern(path.timestampFormat).withZoneUTC().parseMillis(timestampString);
                }
            } catch (IllegalArgumentException e) {
                // to skip
            }
        }
        return timestamp;
    }

    private void toList(final EventNodeRequest request, final EventPath path,
                        final File[] files, final List<StreamEvent> result) throws IOException {
        if (files == null) return;

        for (final File file : files) {
            if (file.isFile()) {
                fileToEvents(request, path, file, result);
            } else {
                toList(request, path, file.listFiles(), result);
            }
        }
    }

    private void fileToEvents(EventNodeRequest request, EventPath path, File file, List<StreamEvent> events) throws IOException {
        final HashSet<String> tags = tagger.get(path.tagsPatterns, file.getAbsolutePath());
        if (request.tags != null && request.tags.size() > 0) {
            if (!tags.containsAll(request.tags)) return;
        }

        if (request.from > 0) {
            if (request.from > file.lastModified()) return;
        }

        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final long timestamp = getTimestamp(path, line);

                boolean goodByFrom = (timestamp == 0 || request.from == 0 || request.from <= timestamp);
                boolean goodByTo = (timestamp == 0 || request.to == 0 || request.to >= timestamp);

                if (goodByFrom && goodByTo) {
                    boolean goodByPattern = true;
                    if (request.pattern != null) {
                        Pattern pattern = Pattern.compile(request.pattern);
                        goodByPattern = pattern.matcher(line).find();
                    }

                    if (goodByPattern)
                        events.add(new StreamEvent(file.getAbsolutePath(), timestamp, line, tags));
                }
            }
        }
    }

    public ArrayList<StreamEvent> get(EventNodeRequest request) throws IOException {
        ArrayList<StreamEvent> events = new ArrayList<>();
        for (EventPath path : request.paths) {
            events.addAll(list(request, path));
        }

        return events;
    }

    public ArrayList<StreamEvent> list(final EventNodeRequest request, final EventPath path) throws IOException {
        ArrayList<StreamEvent> result = new ArrayList<>();
        toList(request, path, new File(path.path).listFiles(), result);
        return result;
    }

}
