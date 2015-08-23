package com.github.terma.logb.node;

import com.github.terma.logb.StreamEvent;
import com.github.terma.logb.node.content.Content;
import com.github.terma.logb.node.content.ContentFactory;
import com.github.terma.logb.node.timestamper.Timestamper;
import com.github.terma.logb.node.timestamper.TimestamperFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EventStreamNode {

    private final Tagger tagger;

    public EventStreamNode(Tagger tagger) {
        this.tagger = tagger;
    }

    private void get(final EventNodeRequest request, final EventPath path,
                     final File[] files, final Timestamper timestamper,
                     final Content content, final List<StreamEvent> result) throws IOException {
        if (files == null) return;

        for (final File file : files) {
            if (file.isFile()) {
                fileToEvents(request, path, file, timestamper, content, result);
            } else {
                get(request, path, file.listFiles(), timestamper, content, result);
            }
        }
    }

    private void fileToEvents(final EventNodeRequest request, final EventPath path,
                              final File file, final Timestamper timestamper,
                              final Content content, final List<StreamEvent> events) throws IOException {
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
                final long timestamp = timestamper.get(line);

                boolean goodByFrom = (timestamp == 0 || request.from == 0 || request.from <= timestamp);
                boolean goodByTo = (timestamp == 0 || request.to == 0 || request.to >= timestamp);

                if (goodByFrom && goodByTo && content.apply(line)) {
                    events.add(new StreamEvent(file.getAbsolutePath(), timestamp, line, tags));
                }
            }
        }
    }

    public ArrayList<StreamEvent> get(final EventNodeRequest request) throws IOException {
        final ArrayList<StreamEvent> events = new ArrayList<>();

        final Content content = ContentFactory.get(request);
        for (final EventPath path : request.paths) {
            final Timestamper timestamper = TimestamperFactory.get(path);
            get(request, path, new File(path.path).listFiles(), timestamper, content, events);
        }

        return events;
    }

}
