package com.github.terma.logb.node;

import com.github.terma.logb.ListItem;
import com.github.terma.logb.LogbService;
import com.github.terma.logb.LogsRequest;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LogbServiceTest {

    private Path testDir;

    @Before
    public void testDirSetup() throws IOException {
        testDir = Files.createTempDirectory("logb-tests");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void testDirRemove() {
        testDir.toFile().delete();
    }

    @Test
    public void ifEmptyPatternsReturnsAllFiles() throws Exception {
        FileUtils.fileWrite(new File(testDir.toFile(), "under.txt").getPath(), "?");

        LogsRequest request = new LogsRequest();
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath());
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(1));
    }

    @Test
    public void onlyFilesForSpecifiedFileNamePattern() throws Exception {
        FileUtils.fileWrite(new File(testDir.toFile(), "under.txt").getPath(), "?");
        FileUtils.fileWrite(new File(testDir.toFile(), "under-momo.txt").getPath(), "?");

        LogsRequest request = new LogsRequest();
        request.fileNamePattern = "momo";
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath() + "/");
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).file, equalTo(testDir.toString() + "/under-momo.txt"));
    }

    @Test
    public void onlyFilesForSpecifiedFileNamePatternWithoutCaseSensetive() throws Exception {
        FileUtils.fileWrite(new File(testDir.toFile(), "under.txt").getPath(), "?");
        FileUtils.fileWrite(new File(testDir.toFile(), "under-momo.txt").getPath(), "?");

        LogsRequest request = new LogsRequest();
        request.fileNamePattern = "MoMo";
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath() + "/");
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).file, equalTo(testDir.toString() + "/under-momo.txt"));
    }

    @Test
    public void onlyFilesWithSpecificLinesIfContentPatternSpecified() throws Exception {
        FileUtils.fileWrite(new File(testDir.toFile(), "numa.txt").getPath(), "-numa-");
        FileUtils.fileWrite(new File(testDir.toFile(), "puma.txt").getPath(), "puma");

        LogsRequest request = new LogsRequest();
        request.contentPattern = "numa";
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath() + "/");
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).file, equalTo(testDir.toString() + "/numa.txt"));
    }

}
