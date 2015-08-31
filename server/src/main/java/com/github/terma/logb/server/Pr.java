package com.github.terma.logb.server;

import com.github.terma.logb.node.EventStreamPath;
import com.github.terma.logb.node.EventStreamRemote;

import java.util.ArrayList;

public class Pr {

    public final EventStreamRemote eventStreamRemote;
    public final ArrayList<EventStreamPath> paths;

    public Pr(EventStreamRemote eventStreamRemote, ArrayList<EventStreamPath> paths) {
        this.eventStreamRemote = eventStreamRemote;
        this.paths = paths;
    }
}
