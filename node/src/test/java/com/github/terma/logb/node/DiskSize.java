package com.github.terma.logb.node;

public enum DiskSize {

    GB(1024 * 1024 * 1024), MB(1024 * 1024), KB(1024);

    private final int bytes;

    DiskSize(int bytes) {
        this.bytes = bytes;
    }

    public static int toBytes(int count, DiskSize diskSize) {
        return count * diskSize.bytes;
    }

}
