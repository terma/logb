package com.github.terma.logb.server;

import com.github.terma.logb.StreamEvent;
import com.github.terma.logb.node.EventStreamRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class EventStream {

    private final EventStreamConfig config;
    private final NodeManager nodeManager;

    public EventStream(EventStreamConfig config, NodeManager nodeManager) {
        this.config = config;
        this.nodeManager = nodeManager;
    }

    public List<StreamEvent> get(EventStreamRequest request) {
        List<String> nodes = config.getAppNodes(request.app);

        List<NodeCall> calls = new ArrayList<>();
        for (String node : nodes) calls.add(new NodeCall(nodeManager, node, request));

        List<StreamEvent> result = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            List<Future<List<StreamEvent>>> futures = executorService.invokeAll(calls);
            for (Future<List<StreamEvent>> future : futures) {
                try {
                    result.addAll(future.get());
                } catch (ExecutionException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static class NodeCall implements Callable<List<StreamEvent>> {

        private final NodeManager nodeManager;
        private final String node;
        private final EventStreamRequest request;

        public NodeCall(NodeManager nodeManager, String node, EventStreamRequest request) {
            this.nodeManager = nodeManager;
            this.node = node;
            this.request = request;
        }

        @Override
        public List<StreamEvent> call() {
            NodeConnection connection = nodeManager.get(node);
            return connection.get(request);
        }
    }

}
