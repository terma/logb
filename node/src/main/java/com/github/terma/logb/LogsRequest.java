package com.github.terma.logb;

import java.io.Serializable;
import java.util.ArrayList;

public class LogsRequest implements Serializable {

    public ArrayList<String> files;
    public String fileNamePattern;
    public String contentPattern;

}
