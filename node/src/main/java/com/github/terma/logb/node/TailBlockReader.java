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
