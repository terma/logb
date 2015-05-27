package com.github.terma.logb;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Logb {

    private final Charset charset = Charset.defaultCharset();
    private RandomAccessFile file;

    public Logb(Path path) throws FileNotFoundException {
        this.file = new RandomAccessFile(path.toFile(), "r");
    }

    public Logb(File file) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, "r");
    }

    public Logb(String file) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, "r");
    }

    public long getPosition() {
        try {
            return file.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getMaxPosition() {
        try {
            return file.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void seek(long position) {
        try {
            file.seek(position);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FilePiece getPiece(long start, int length) throws IOException {
//        final long length = file.length();
//        if (start >= length) return new FilePiece(start, start, "");

        file.seek(start);
        byte[] buffer = new byte[length];
        int done = file.read(buffer);
        int realLength = done < 0 ? 0 : done;
        return new FilePiece(start, realLength, new String(buffer, 0, realLength, charset));
    }

    public List<String> getLines(int lines) throws IOException {
        List<String> result = new ArrayList<String>(lines);

        final ByteArrayOutputStream lineBuf = new ByteArrayOutputStream(64);
        int num;
        boolean seenCR = false;
        final byte[] buffer = new byte[64];

        while (((num = file.read(buffer)) != -1)) {
            for (int i = 0; i < num; i++) {
                final byte ch = buffer[i];
                switch (ch) {
                    case '\n':
                        seenCR = false; // swallow CR before LF
                        result.add(new String(lineBuf.toByteArray(), charset));
                        if (result.size() >= lines) return result;

                        lineBuf.reset();
                        break;
                    case '\r':
                        if (seenCR) {
                            lineBuf.write('\r');
                        }
                        seenCR = true;
                        break;
                    default:
                        if (seenCR) {
                            seenCR = false; // swallow final CR
                            result.add(new String(lineBuf.toByteArray(), charset));
                            if (result.size() >= lines) return result;

                            lineBuf.reset();
                        }
                        lineBuf.write(ch);
                }
            }
        }

        if (lineBuf.size() > 0) {
            result.add(new String(lineBuf.toByteArray(), charset));
        }

        return result;
    }

    public void close() {
        try {
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
