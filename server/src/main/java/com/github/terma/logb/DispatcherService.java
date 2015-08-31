/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.logb;

import com.github.terma.logb.config.Config;
import com.github.terma.logb.config.ConfigApp;
import com.github.terma.logb.config.ConfigServer;
import com.github.terma.logb.config.ConfigService;
import com.github.terma.logb.node.EventStreamRemote;
import com.github.terma.logb.node.EventStreamRequest;
import com.github.terma.logb.server.Pr;

import java.io.InputStream;
import java.util.*;

public class DispatcherService {

    public static final DispatcherService INSTANCE = new DispatcherService();

    public static final Config config = ConfigService.get();
    private final Map<String, RemoteService> services = new HashMap<>();

    private DispatcherService() {
    }

    public List<Pr> getEventStreamRemotes(EventStreamRequest request, final InputStream jar) {
        final ConfigApp app = config.findApp(request.app);
        final List<Pr> result = new ArrayList<>();
        for (final ConfigServer server : app.servers) {
            if (server.host != null) {
                result.add(new Pr(getService(server, jar).getEventStreamRemote(), server.paths));
            } else {
                result.add(new Pr(new LocalService().getEventStreamRemote(), server.paths));
            }
        }
        return result;
    }

    public List<ListItem> list(final ListRequest request, final InputStream jar) {
        final ConfigApp app = config.findApp(request.app);
        final List<ListItem> result = new ArrayList<>();
        for (final ConfigServer server : app.servers) {
            request.files = new ArrayList<>(server.files);
            if (server.host != null) {
                result.addAll(getService(server, jar).list(request));
            } else {
                result.addAll(new LocalService().list(request));
            }
        }
        return result;
    }

    public FilePiece piece(final LogRequest request, final InputStream jar) {
        final ConfigApp app = config.findApp(request.app);
        final ConfigServer server = findServer(app, request.host);
        if (server.host != null) return getService(server, jar).piece(request);
        else return new LocalService().piece(request);
    }

    private synchronized RemoteService getService(final ConfigServer server, final InputStream jar) {
        RemoteService remoteService = services.get(server.host);
        if (remoteService == null) {
            remoteService = new RemoteService(server, jar);
            services.put(server.host, remoteService);
        }
        return remoteService;
    }

    private ConfigServer findServer(ConfigApp app, String host) {
        for (ConfigServer server : app.servers) {
            if (Objects.equals(server.host, host)) return server;
        }
        throw new IllegalArgumentException("Can't find server: " + host + "!");
    }

}
