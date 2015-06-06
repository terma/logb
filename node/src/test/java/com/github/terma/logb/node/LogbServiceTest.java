package com.github.terma.logb.node;

import com.github.terma.logb.ListItem;
import com.github.terma.logb.ListRequest;
import com.github.terma.logb.LogbService;
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

        ListRequest request = new ListRequest();
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath());
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(1));
    }

    @Test
    public void onlyFilesForSpecifiedFileNamePattern() throws Exception {
        FileUtils.fileWrite(new File(testDir.toFile(), "under.txt").getPath(), "?");
        FileUtils.fileWrite(new File(testDir.toFile(), "under-momo.txt").getPath(), "?");

        ListRequest request = new ListRequest();
        request.fileName = "momo";
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

        ListRequest request = new ListRequest();
        request.fileName = "MoMo";
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

        ListRequest request = new ListRequest();
        request.content = "numa";
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath() + "/");
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).file, equalTo(testDir.toString() + "/numa.txt"));
    }

    @Test
    public void sortListResultByLastModification() throws Exception {
        File fileB = new File(testDir.toFile(), "b.txt");
        FileUtils.fileWrite(fileB.getPath(), "");
        File fileA = new File(testDir.toFile(), "a.txt");
        FileUtils.fileWrite(fileA.getPath(), "");
        File fileC = new File(testDir.toFile(), "c.txt");
        FileUtils.fileWrite(fileC.getPath(), "");

        fileB.setLastModified(System.currentTimeMillis() - 20000);
        fileA.setLastModified(System.currentTimeMillis() - 10000);
        fileC.setLastModified(System.currentTimeMillis());

        ListRequest request = new ListRequest();
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath());
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(3));
        assertThat(result.get(0).file, equalTo(testDir.toString() + "/c.txt"));
        assertThat(result.get(1).file, equalTo(testDir.toString() + "/a.txt"));
        assertThat(result.get(2).file, equalTo(testDir.toString() + "/b.txt"));
    }

    @Test
    public void listNoMore100Results() throws Exception {
        for (int i = 0; i < 120; i++) {
            FileUtils.fileWrite(new File(testDir.toFile(), i + ".txt").getPath(), "");
        }

        ListRequest request = new ListRequest();
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath());
        List<ListItem> result = new LogbService().list(request);

        assertThat(result.size(), equalTo(100));
    }

}
