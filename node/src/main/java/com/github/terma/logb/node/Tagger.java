package com.github.terma.logb.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tagger {

    public HashSet<String> get(ArrayList<String> tagsPatterns, String message) {
        HashSet<String> result = new HashSet<>();

        if (tagsPatterns != null && message != null) {
            for (String tagPattern : tagsPatterns) {
                Pattern pattern = Pattern.compile(tagPattern);
                Matcher matcher = pattern.matcher(message);
                while (matcher.find()) {
                    result.add(matcher.group());
                }
            }
        }

        return result;
    }

}
