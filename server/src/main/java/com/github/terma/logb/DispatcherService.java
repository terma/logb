package com.github.terma.logb;

import com.github.terma.logb.config.Config;
import com.github.terma.logb.config.ConfigApp;
import com.github.terma.logb.config.ConfigServer;
import com.github.terma.logb.config.ConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DispatcherService {

    private final Config config = ConfigService.get();

    public List<ListItem> list(final String appName) {
        final ConfigApp app = findApp(appName);
        final List<ListItem> result = new ArrayList<>();
        for (ConfigServer server : app.servers) {
            if (server.host != null) {
                result.addAll(new ServerService(server.host).list(server.files));
            } else {
                result.addAll(new LocalService().list(server.files));
            }
        }
        return result;
    }

    public FilePiece piece(final LogRequest request) {
        final ConfigApp app = findApp(request.app);
        final ConfigServer server = findServer(app, request.host);
        if (server.host != null) return new ServerService(server.host).piece(request);
        else return new LocalService().piece(request);
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
