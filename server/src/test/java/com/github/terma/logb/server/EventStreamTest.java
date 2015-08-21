package com.github.terma.logb.server;

import com.github.terma.logb.StreamEvent;
import org.junit.Test;

import java.util.List;

public class EventStreamTest {

    @Test
    public void sendRequestToNodes() {
        EventStream eventStream = new EventStream(null, null);

        EventStreamRequest request = new EventStreamRequest();
        List<StreamEvent> response = eventStream.get(request);


    }

}
