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

package com.github.terma.logb.node;

import com.github.terma.logb.StreamEvent;
import com.github.terma.logb.node.content.Content;
import com.github.terma.logb.node.content.ContentFactory;
import com.github.terma.logb.node.timestamper.Timestamper;
import com.github.terma.logb.node.timestamper.TimestamperFactory;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class EventStreamNode implements EventStreamRemote {

    private final RealTagger tagger;

    public EventStreamNode() {
        this(new RealTagger());
    }

    public EventStreamNode(final RealTagger tagger) {
        this.tagger = tagger;
    }

    private void get(final EventStreamRequest request, final EventStreamPath path,
                     final File[] files, final Timestamper timestamper,
                     final Content content, final List<StreamEvent> result) {
        if (files == null) return;

        for (final File file : files) {
            if (file.isFile()) {
                fileToEvents(request, path, file, timestamper, content, result);
            } else {
                get(request, path, file.listFiles(), timestamper, content, result);
            }
        }
    }

    private void getTags(final EventStreamPath path, final File[] files, final HashSet<String> result) {
        if (files == null) return;

        for (final File file : files) {
            if (file.isFile()) {
                final HashSet<String> tags = tagger.get(path.tagsPatterns, file.getAbsolutePath());
                result.addAll(tags);
            } else {
                getTags(path, file.listFiles(), result);
            }
        }
    }

    private void fileToEvents(final EventStreamRequest request, final EventStreamPath path,
                              final File file, final Timestamper timestamper,
                              final Content content, final List<StreamEvent> events) {
        final HashSet<String> tags = tagger.get(path.tagsPatterns, file.getAbsolutePath());

        if (request.tags != null && request.tags.size() > 0) {
            if (!tags.containsAll(request.tags)) return;
        }

        if (request.from > 0) {
            if (request.from > file.lastModified()) return;
        }

        try (final ReverseLineReader reader = new ReverseLineReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                final long timestamp = timestamper.get(line);

                boolean goodByFrom = (timestamp == 0 || request.from == 0 || request.from <= timestamp);
                boolean goodByTo = (timestamp == 0 || request.to == 0 || request.to >= timestamp);

                if (!goodByFrom) break;

                if (goodByTo && content.apply(line)) {
                    events.add(new StreamEvent(file.getAbsolutePath(), timestamp, line, tags));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.reverse(events);
    }

    @Override
    public ArrayList<StreamEvent> get(
            final EventStreamRequest request, final ArrayList<EventStreamPath> paths) throws RemoteException {
        final ArrayList<StreamEvent> events = new ArrayList<>();

        final Content content = ContentFactory.get(request);
        for (final EventStreamPath path : paths) {
            final Timestamper timestamper = TimestamperFactory.get(path);
            get(request, path, new File(path.path).listFiles(), timestamper, content, events);
        }

        return events;
    }

    @Override
    public HashSet<String> getTags(final ArrayList<EventStreamPath> paths) throws RemoteException {
        final HashSet<String> result = new HashSet<>();
        for (final EventStreamPath path : paths) {
            getTags(path, new File(path.path).listFiles(), result);
        }
        return result;
    }

}
