package com.github.terma.logb.node;

import com.github.terma.logb.StreamEvent;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.github.terma.logb.node.CollectionUtils.al;
import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EventStreamNodeTest extends TempFile {

    private Tagger tagger = new Tagger();
    private EventNodeRequest request = new EventNodeRequest();
    private EventPath path = new EventPath();
    private EventStreamNode node = new EventStreamNode(tagger);

    @Test
    public void getZeroEventsIfNoPathsForRequest() throws IOException {
        List<StreamEvent> events = node.get(request);
        assertEquals(new ArrayList<>(), events);
    }

    @Test
    public void getAllEventsFromIfNoFilterCriteria() throws Exception {
        createFile("a", "text");

        path.path = testDir.toFile().getAbsolutePath();
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
    }

    @Test
    public void getEventsWithZeroTimestampIfNoTimestampPattern() throws Exception {
        createFile("a", "text");

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampFormat = "X";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(0, events.get(0).timestamp);
    }

    @Test
    public void getEventsWithZeroTimestampIfNoTimestampFormat() throws Exception {
        createFile("a", "text");

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampPattern = "X";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(0, events.get(0).timestamp);
    }

    @Test
    public void getEventsWithZeroTimestampIfIncorrectPattern() throws Exception {
        createFile("a", "text");

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampPattern = "(";
        path.timestampFormat = "X";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(0, events.get(0).timestamp);
    }

    @Test
    public void getEventsWithZeroTimestampIfIncorrectFormat() throws Exception {
        createFile("a", "T text");

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampPattern = "T";
        path.timestampFormat = "X";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(0, events.get(0).timestamp);
    }

    @Test
    public void getAllEventsIfIncorrectSearch() throws Exception {
        createFile("a", "T text");

        path.path = testDir.toFile().getAbsolutePath();
        request.pattern = "(";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
    }

    @Test
    public void getEventsWithTimestampByUTFIfTimestampPresentAndCantPatternAndFormat() throws Exception {
        createFile("a", "2015-05-01 text");

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampPattern = "\\d{4}-\\d{2}-\\d{2}";
        path.timestampFormat = "yyyy-MM-dd";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(
                DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC().parseMillis("2015-05-01"),
                events.get(0).timestamp);
    }

    @Test
    public void getZeroEventsIfEmptyFiles() throws Exception {
        createFile("b", "");

        path.path = testDir.toFile().getAbsolutePath();
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(new ArrayList<StreamEvent>(), events);
    }

    @Test
    public void getZeroEventsIfPathNotExistent() throws Exception {
        path.path = "/x/x";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(new ArrayList<StreamEvent>(), events);
    }

    @Test
    @Ignore
    public void getEventsWithTagsFromFileName() throws Exception {
        createFile("A", "1\n2");
        createFile("B", "3\n4");

        ArrayList<String> tagPatterns = new ArrayList<>();

        when(tagger.get(same(tagPatterns), eq(testDir.toFile().getAbsolutePath() + "/A")))
                .thenReturn(new HashSet<>(singletonList("A")));

        when(tagger.get(same(tagPatterns), eq(testDir.toFile().getAbsolutePath() + "/B")))
                .thenReturn(new HashSet<>(singletonList("B")));

        path.path = testDir.toFile().getAbsolutePath();
        path.tagsPatterns = tagPatterns;
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(4, events.size());
        assertEquals(new HashSet<>(singletonList("A")), events.get(0).tags);
        assertEquals(new HashSet<>(singletonList("A")), events.get(1).tags);
        assertEquals(new HashSet<>(singletonList("B")), events.get(2).tags);
        assertEquals(new HashSet<>(singletonList("B")), events.get(3).tags);
    }

    @Test
    public void filterEventsByTags() throws Exception {
        createFile("A", "1");
        createFile("B", "3");

        path.path = testDir.toFile().getAbsolutePath();
        path.tagsPatterns = al("[AB]");

        request.tags = al("A");

        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(new HashSet<>(singletonList("A")), events.get(0).tags);
    }

    @Test
    public void filterEventsByTimeperiodWhenLastModificationInPast() throws Exception {
        final DateTime now = DateTime.now();

        createFile("A", "1", now.minusDays(1).getMillis());
        createFile("B", "3", now.getMillis());

        path.path = testDir.toFile().getAbsolutePath();
        request.from = now.minusHours(1).getMillis();
        request.to = now.plusHours(1).getMillis();
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals("3", events.get(0).message);
    }

    @Test
    public void filterEventsByTimeperiod() throws Exception {
        final DateTime now = DateTime.now();
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZoneUTC();

        createFile("A", formatter.print(now) + " message1\n" + formatter.print(now.minusDays(1)) + " message2");

        path.path = testDir.toFile().getAbsolutePath();
        path.timestampFormat = "yyyy-MM-dd HH:mm";
        path.timestampPattern = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})";
        request.from = now.minusHours(1).getMillis();
        request.to = now.plusHours(1).getMillis();
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals(formatter.print(now) + " message1", events.get(0).message);
    }

    @Test
    public void filterEventsByMessage() throws Exception {
        createFile("A", "msg1\nmessage2");

        path.path = testDir.toFile().getAbsolutePath();
        request.pattern = "msg";
        request.paths.add(path);

        List<StreamEvent> events = node.get(request);
        assertEquals(1, events.size());
        assertEquals("msg1", events.get(0).message);
    }

}
