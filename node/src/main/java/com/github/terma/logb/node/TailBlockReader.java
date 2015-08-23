package com.github.terma.logb.node;

import java.io.*;
import java.util.Arrays;

public class TailBlockReader implements Closeable {

    private final int blockSize = 5 * 1024 * 1024; // bytes
    private final RandomAccessFile randomAccessFile;

    private long position = -1;

    public TailBlockReader(final File file) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "r");
    }

    public byte[] readFromEnd() throws IOException {
        if (position < 0) {
            position = randomAccessFile.length();
        }

        final long startPosition = Math.max(position - blockSize, 0);

        final byte[] block = new byte[(int) position - (int) startPosition];
        randomAccessFile.seek(startPosition);
        final int read = randomAccessFile.read(block);

        position = startPosition;

        return Arrays.copyOf(block, read);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

}
