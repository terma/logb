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

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempFile {

    protected Path testDir;

    @Before
    public void testDirSetup() throws IOException {
        testDir = Files.createTempDirectory("logb-tests");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void testDirRemove() {
        testDir.toFile().delete();
    }

    protected File createFile(String name, String content) throws Exception {
        final File file = new File(testDir.toFile(), name);
        FileUtils.fileWrite(file.getPath(), content);
        return file;
    }

    protected void createFile(String name, String content, long lastModification) throws Exception {
        final File file = new File(testDir.toFile(), name);
        FileUtils.fileWrite(file.getPath(), content);
        Assert.assertTrue("Can't set lastModification for " + name, file.setLastModified(lastModification));
    }

}
