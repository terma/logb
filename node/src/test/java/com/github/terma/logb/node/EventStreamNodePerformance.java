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
import org.hamcrest.Matchers;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Ignore
public class EventStreamNodePerformance extends TempFile {

    private final ArrayList<EventStreamPath> paths = new ArrayList<>();
    private RealTagger tagger = new RealTagger();
    private EventStreamRequest request = new EventStreamRequest();
    private EventStreamPath path = new EventStreamPath();
    private EventStreamNode node = new EventStreamNode(tagger);

    @Test
    public void getEventsForBigFile() throws Exception {

        int times = 5;
        int requiredSize = DiskSize.toBytes(100, DiskSize.MB);

        generateFiles(1, requiredSize);

        path.path = testDir.toFile().getAbsolutePath();
        paths.add(path);

        final long start = System.currentTimeMillis();
        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertTrue(events.size() > 1000);
        }
        final long time = (System.currentTimeMillis() - start) / times;

        System.out.println("average time for " + requiredSize + " is " + time + " msec");
        Assert.assertThat(time, Matchers.lessThan(1900L));
    }

    @Test
    public void getEventsForBigFileWhenTimeOnlyForSmallPercent() throws Exception {
        final int times = 5;
        final int requiredSize = DiskSize.toBytes(100, DiskSize.MB);

        final long now = System.currentTimeMillis();
        generateFiles(1, requiredSize, now - TimeUnit.HOURS.toMillis(1), now);

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampPattern = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
        path.timestampFormat = "yyyy-MM-dd HH:mm:ss";
        request.from = now - TimeUnit.MINUTES.toMillis(5);
        request.to = now + TimeUnit.MINUTES.toMillis(5);
        paths.add(path);

        final long start = System.currentTimeMillis();
        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertTrue("Find zero events!", events.size() > 0);
        }
        final long time = (System.currentTimeMillis() - start) / times;

        System.out.println("average time for " + requiredSize + " bytes is " + time + " msec");
        Assert.assertThat(time, Matchers.lessThan(500L));
    }

    @Test
    public void noFilteringManySmallFiles() throws Exception {
        long start = System.currentTimeMillis();

        int times = 5;
        int files = 1000;
        int messages = 1024;

        generateFiles(files, messages);

        path.path = testDir.toFile().getAbsolutePath();
        paths.add(path);

        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertEquals(messages * files, events.size());
        }

        System.out.println("test files: " + files + ", messages: " + messages + " average get events " + (System.currentTimeMillis() - start) / times + " msec");
    }

    @Test
    public void manySmallFilesWithTags() throws Exception {
        int times = 5;
        int files = 1000;
        int requiredSize = DiskSize.toBytes(100, DiskSize.KB);

        generateFiles(files, requiredSize);

        path.path = testDir.toFile().getAbsolutePath();
        path.tagsPatterns.add("_\\d+_");
        paths.add(path);

        final long start = System.currentTimeMillis();
        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertTrue(events.size() > 0);

            for (StreamEvent event : events) {
                Assert.assertTrue(event.tags.size() > 0);
            }
        }
        final long time = (System.currentTimeMillis() - start) / times;

        System.out.println("test files: " + files + ", messages: " + requiredSize + " average get events " + time + " msec");
        Assert.assertThat(time, Matchers.lessThan(2900L));
    }

    @Test
    public void manySmallFilesWithTimestamp() throws Exception {
        int times = 5;
        int files = 1000;
        int messages = 1024;

        generateFiles(files, messages);

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampPattern = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
        path.timestampFormat = "yyyy-MM-dd HH:mm:ss";
        paths.add(path);

        final long start = System.currentTimeMillis();
        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertEquals(messages * files, events.size());

            for (StreamEvent event : events) {
                Assert.assertTrue(event.timestamp > 0);
            }
        }
        final long time = System.currentTimeMillis() - start;

        System.out.println("test files: " + files + ", messages: " + messages + " average get events " + time / times + " msec");
    }

    @Test
    public void searchByPatternInManySmallFiles() throws Exception {
        int times = 5;
        int files = 1000;
        int messages = 1024;

        generateFiles(files, messages);

        path.path = testDir.toFile().getAbsolutePath();
        request.pattern = "(index 1)";
        paths.add(path);

        final long start = System.currentTimeMillis();
        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertTrue(events.size() > 0);
        }
        final long time = System.currentTimeMillis() - start;

        System.out.println("test files: " + files + ", messages: " + messages + " average get events " + time / times + " msec");
    }

    @Test
    public void searchByTimestampWhenManySmallFileInPast() throws Exception {
        int times = 5;
        int files = 1000;
        int messages = 1024;

        generateFiles(files, messages);

        path.path = testDir.toFile().getAbsolutePath();
        request.from = System.currentTimeMillis() + HOURS.toMillis(2);
        paths.add(path);

        final long start = System.currentTimeMillis();
        for (int t = 0; t < times; t++) {
            List<StreamEvent> events = node.get(request, paths);
            assertEquals(0, events.size());
        }
        final long time = System.currentTimeMillis() - start;

        System.out.println("test files: " + files + ", messages: " + messages + " average get events " + time / times + " msec");
    }

    private void appendMessage(StringBuilder text, String timestamp, int index) {
        text.append(timestamp)
                .append(" event ")
                .append(System.currentTimeMillis())
                .append(" msec and index ")
                .append(index).append("\n");
    }

    private void generateFiles(int files, int requiredSize, final long from, final long to) throws Exception {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();

        final StringBuilder test = new StringBuilder();
        appendMessage(test, dateTimeFormatter.print(System.currentTimeMillis()), 0);
        final long messageSize = test.length();
        final int messageCount = (int) (requiredSize / messageSize);

        final double delta = (float) (to - from) / messageCount;

        for (int f = 0; f < files; f++) {
            final StringBuilder fileContent = new StringBuilder();
            for (int index = 0; index < messageCount; index++) {
                appendMessage(fileContent, dateTimeFormatter.print(Math.round((double) from + delta * (double) index)), index);
            }

            createFile("file_" + f + "_.log", fileContent.toString());
        }
    }

    private void generateFiles(int files, int requiredSize) throws Exception {
        generateFiles(files, requiredSize, System.currentTimeMillis(), System.currentTimeMillis());
    }

}
