package com.github.terma.logb.node.content;

import java.util.regex.Pattern;

public class FilteredContent implements Content {

    private final Pattern pattern;

    public FilteredContent(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean apply(String line) {
        return pattern.matcher(line).find();
    }
}
