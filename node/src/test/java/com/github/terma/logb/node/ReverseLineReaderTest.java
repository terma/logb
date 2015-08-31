package com.github.terma.logb.node;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

public class ReverseLineReaderTest extends TempFile {

    @Test
    public void getNullLineIfEmptyFile() throws Exception {
        File file = createFile("f", "");

        ReverseLineReader reverseLineReader = new ReverseLineReader(file);
        Assert.assertNull(reverseLineReader.readLine());
        Assert.assertNull(reverseLineReader.readLine());
    }

    @Test
    public void getLineIfFileOneLine() throws Exception {
        File file = createFile("f", "aaaaa");

        ReverseLineReader reverseLineReader = new ReverseLineReader(file);
        Assert.assertEquals("aaaaa", reverseLineReader.readLine());
        Assert.assertNull(reverseLineReader.readLine());
    }

    @Test
    public void getMultiLines() throws Exception {
        File file = createFile("f", "1\n2\n333");

        ReverseLineReader reverseLineReader = new ReverseLineReader(file);
        Assert.assertEquals("333", reverseLineReader.readLine());
        Assert.assertEquals("2", reverseLineReader.readLine());
        Assert.assertEquals("1", reverseLineReader.readLine());
        Assert.assertNull(reverseLineReader.readLine());
    }

    @Test
    public void getEmptyLine() throws Exception {
        File file = createFile("f", "\n");

        ReverseLineReader reverseLineReader = new ReverseLineReader(file);
        Assert.assertEquals("", reverseLineReader.readLine());
        Assert.assertNull(reverseLineReader.readLine());
    }

}
