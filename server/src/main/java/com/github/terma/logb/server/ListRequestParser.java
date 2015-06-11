package com.github.terma.logb.server;

import com.github.terma.logb.*;
import com.github.terma.logb.criteria.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ListRequestParser {

    private static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(CriteriaRequest.class)
                    .registerSubtype(PlainCriteriaRequest.class, "plain")
                    .registerSubtype(AndCriteriaRequest.class, "and")
                    .registerSubtype(OrCriteriaRequest.class, "or")
                    .registerSubtype(RegexCriteriaRequest.class, "regex")).create();

    public static ListRequest parse(String string) {
        if (string == null) return new ListRequest();
        return gson.fromJson(string, ListRequest.class);
    }

}
