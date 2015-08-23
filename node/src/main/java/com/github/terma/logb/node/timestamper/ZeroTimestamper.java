package com.github.terma.logb.node.timestamper;

class ZeroTimestamper implements Timestamper {

    @Override
    public long get(String line) {
        return 0;
    }

}
