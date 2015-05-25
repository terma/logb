package com.github.terma.logb;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LogbTest {

    private Path tempFile;

    @Before
    public void before() throws IOException {
        tempFile = Files.createTempFile("logb", "tests");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void after() {
        tempFile.toFile().delete();
    }

    @Test
    public void shouldReturnZeroAsStartPosition() throws FileNotFoundException {
        Logb logb = new Logb(tempFile);

        assertThat(logb.getPosition(), equalTo(0L));
    }

    @Test
    public void shouldReturnMaxPositionInFile() throws Exception {
        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "aaaa");

        Logb logb = new Logb(tempFile);

        assertThat(logb.getMaxPosition(), equalTo(4L));
    }

    @Test
    public void shouldReturnUpdatedMaxPositionInFileAfterChange() throws Exception {
        Logb logb = new Logb(tempFile);

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "aaaa");
        assertThat(logb.getMaxPosition(), equalTo(4L));

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "bb");
        assertThat(logb.getMaxPosition(), equalTo(2L));
    }

    @Test
    public void shouldBeAbleToChangePosition() throws Exception {
        Logb logb = new Logb(tempFile);

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "aaaa");

        assertThat(logb.getPosition(), equalTo(0L));
        logb.seek(2);

        assertThat(logb.getPosition(), equalTo(2L));
    }

    @Test
    public void shouldGetLinesStartFromCurrentPosition() throws Exception {
        Logb logb = new Logb(tempFile);

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "a\nb\ncc\nddd\n");

        assertThat(
                logb.getLines(3),
                equalTo(Arrays.asList("a", "b", "cc")));

    }

    @Test
    public void shouldGetAllLinesIfRequestMore() throws Exception {
        Logb logb = new Logb(tempFile);

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "a\nb");

        assertThat(
                logb.getLines(10),
                equalTo(Arrays.asList("a", "b")));

    }

}
