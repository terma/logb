package com.github.terma.logb.node;

import com.github.terma.logb.ListItem;
import com.github.terma.logb.LogbService;
import com.github.terma.logb.ListRequest;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LogbServicePerformance {

    private Path testDir;

    @Before
    public void testDirSetup() throws Exception {
        testDir = Files.createTempDirectory("logb-performance");

        for (int i = 0; i < 1000; i++) {
            FileUtils.fileWrite(new File(testDir.toFile(), "numa" + i).getPath(), "content of file with numa");
        }
        for (int i = 0; i < 1000; i++) {
            FileUtils.fileWrite(new File(testDir.toFile(), "not-numa" + i).getPath(), "content of file without search word");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void testDirRemove() {
        testDir.toFile().delete();
    }

    @Test
    public void onlyFilesWithSpecificLinesIfContentPatternSpecified() throws Exception {
        assertThat(testDir.toFile().list().length, equalTo(2000));

        ListRequest request = new ListRequest();
//        request.content = "numa";
        request.files = new ArrayList<>();
        request.files.add(testDir.toFile().getPath());

        // hot
        System.out.println(new LogbService().list(request).size());
        System.out.println(new LogbService().list(request).size());

        // test
        long start = System.currentTimeMillis();
        List<ListItem> result = new LogbService().list(request);
        System.out.println("Time " + (System.currentTimeMillis() - start) + " msec");

        assertThat(result.size(), equalTo(1000));
    }

}
