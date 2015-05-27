package com.github.terma.logb;

import java.io.Serializable;

public class LogRequest implements Serializable {

    public String app;
    public String host;
    public String file;
    public long start;
    public int length;

}
