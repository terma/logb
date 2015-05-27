package com.github.terma.logb;

import java.io.Serializable;

public class FilePiece implements Serializable {

    public final long start;
    public final long length;
    public final String content;

    public FilePiece(long start, long length, String s) {
        this.start = start;
        this.length = length;
        this.content = s;
    }

    @Override
    public String toString() {
        return "FilePiece{" +
                "start=" + start +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilePiece filePiece = (FilePiece) o;

        if (length != filePiece.length) return false;
        if (start != filePiece.start) return false;
        if (!content.equals(filePiece.content)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (length ^ (length >>> 32));
        result = 31 * result + content.hashCode();
        return result;
    }

}
