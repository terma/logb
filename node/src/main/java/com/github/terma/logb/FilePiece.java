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
