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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class Logb {

    private final Charset charset = Charset.defaultCharset();
    private RandomAccessFile fileAccess;
    private final File file;

    public Logb(File fileAccess) throws FileNotFoundException {
        this.file = fileAccess;
        this.fileAccess = new RandomAccessFile(fileAccess, "r");
    }

    public Logb(String fileAccess) throws FileNotFoundException {
        this(new File(fileAccess));
    }

    public FilePiece getPiece(Long start, int length) throws IOException {
//        final long length = file.length();
//        if (start >= length) return new FilePiece(start, start, "");

        long realStart = start != null ? start : Math.max(0, file.length() - length);

        fileAccess.seek(realStart);
        byte[] buffer = new byte[length];
        int done = fileAccess.read(buffer);
        int realLength = done < 0 ? 0 : done;
        return new FilePiece(realStart, file.length(), file.lastModified(), new String(buffer, 0, realLength, charset));
    }

//    public List<String> getLines(int lines) throws IOException {
//        List<String> result = new ArrayList<String>(lines);
//
//        final ByteArrayOutputStream lineBuf = new ByteArrayOutputStream(64);
//        int num;
//        boolean seenCR = false;
//        final byte[] buffer = new byte[64];
//
//        while (((num = fileAccess.read(buffer)) != -1)) {
//            for (int i = 0; i < num; i++) {
//                final byte ch = buffer[i];
//                switch (ch) {
//                    case '\n':
//                        seenCR = false; // swallow CR before LF
//                        result.add(new String(lineBuf.toByteArray(), charset));
//                        if (result.size() >= lines) return result;
//
//                        lineBuf.reset();
//                        break;
//                    case '\r':
//                        if (seenCR) {
//                            lineBuf.write('\r');
//                        }
//                        seenCR = true;
//                        break;
//                    default:
//                        if (seenCR) {
//                            seenCR = false; // swallow final CR
//                            result.add(new String(lineBuf.toByteArray(), charset));
//                            if (result.size() >= lines) return result;
//
//                            lineBuf.reset();
//                        }
//                        lineBuf.write(ch);
//                }
//            }
//        }
//
//        if (lineBuf.size() > 0) {
//            result.add(new String(lineBuf.toByteArray(), charset));
//        }
//
//        return result;
//    }

    public void close() {
        try {
            fileAccess.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
