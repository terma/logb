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
    public void shouldReturnEmptyPieceIfStartMoreLength() throws Exception {
        Logb logb = new Logb(tempFile);

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "aaa");

        assertThat(logb.getPiece(10, 12), equalTo(new FilePiece(10, 0, "")));
    }

    @Test
    public void shouldReturnEmptyPieceIfLengthMoreReal() throws Exception {
        Logb logb = new Logb(tempFile);

        FileUtils.fileWrite(tempFile.toFile().getAbsolutePath(), "aaa");

        assertThat(logb.getPiece(1, 12), equalTo(new FilePiece(1, 2, "aa")));
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
