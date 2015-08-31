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

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class ReverseLineReader implements Closeable {

    private final TailBlockReader tailBlockReader;

    private byte[] block;
    private int blockEnd;

    public ReverseLineReader(File file) throws FileNotFoundException {
        this.tailBlockReader = new TailBlockReader(file);
    }

    public String readLine() throws IOException {
        if (block == null) {
            byte[] tempBlock = tailBlockReader.readFromEnd();
            if (tempBlock.length == 0) return null;
            block = tempBlock;
            blockEnd = block.length - 1;
        }

        if (blockEnd < 0) {
            byte[] tempBlock = tailBlockReader.readFromEnd();
            if (tempBlock.length == 0) return null;
            block = tempBlock;
            blockEnd = block.length - 1;
        }

        int index = blockEnd;
        for (; index > -1; index--) {
            char c = (char) block[index];
            if (c == '\n') {
                String line = new String(block, index + 1, blockEnd - index, Charset.forName("ASCII"));
                blockEnd = index - 1;
                return line;
            }
        }

        if (index < 0) { // can't find any new line symbol
            String result = new String(block, 0, blockEnd + 1);
            blockEnd = -1;
            return result;
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        tailBlockReader.close();
    }

}
