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

import java.io.InputStream;
import java.util.*;

public class DispatcherService {

    public static final DispatcherService INSTANCE = new DispatcherService();

    private final Config config = ConfigService.get();
    private final Map<String, RemoteService> services = new HashMap<>();

    private DispatcherService() {
    }

    public List<ListItem> list(final String appName, final InputStream jar) {
        final ConfigApp app = findApp(appName);
        final List<ListItem> result = new ArrayList<>();
        for (final ConfigServer server : app.servers) {
            if (server.host != null) {
                result.addAll(getService(server, jar).list(server.files));
            } else {
                result.addAll(new LocalService().list(server.files));
            }
        }
        return result;
    }

    public FilePiece piece(final LogRequest request, final InputStream jar) {
        final ConfigApp app = findApp(request.app);
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

    private ConfigApp findApp(String appName) {
        for (ConfigApp app : config.apps) {
            if (app.name.equals(appName)) return app;
        }
        throw new IllegalArgumentException("Can't find app: " + appName + "!");
    }

    private ConfigServer findServer(ConfigApp app, String host) {
        for (ConfigServer server : app.servers) {
            if (Objects.equals(server.host, host)) return server;
        }
        throw new IllegalArgumentException("Can't find server: " + host + "!");
    }

}