package com.github.terma.logb;

import java.io.Serializable;
import java.util.List;

public class ListRequest implements Serializable {

    public String app;
    public List<String> files;
    public String fileName;
    public String content;

}
