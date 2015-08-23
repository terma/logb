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
