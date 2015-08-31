package com.github.terma.logb.node.content;

import com.github.terma.logb.node.EventStreamRequest;

public class ContentFactory {

    public static Content get(EventStreamRequest request) {
        try {
            return new FilteredContent(request.pattern);
        } catch (NullPointerException | IllegalArgumentException e) {
            return new AllContent();
        }
    }

}
