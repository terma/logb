package com.github.terma.logb.config;

import com.github.terma.logb.node.EventStreamPath;

import java.util.ArrayList;
import java.util.List;

public class ConfigServer {

    public String host;
    public String privateKeyFile;
    public String user;
    public List<String> files = new ArrayList<>();

    public ArrayList<EventStreamPath> paths = new ArrayList<>();

    @Override
    public String toString() {
        return user + "@" + host + (privateKeyFile != null ? " pk: " + privateKeyFile : "");
    }

}
