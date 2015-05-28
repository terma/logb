package com.github.terma.logb.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigServer {

    public String host;

    public String privateKeyFile;
    public String user;
    public List<String> files = new ArrayList<>();


    @Override
    public String toString() {
        return "ConfigServer{" +
                "host='" + host + '\'' +
                ", privateKeyFile='" + privateKeyFile + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

}
