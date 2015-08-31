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

import com.github.terma.logb.config.ConfigServer;
import com.github.terma.logb.node.EventStreamRemote;

import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RemoteService {

    private final String host;
    private final LogbRemote logbRemote;
    private final EventStreamRemote eventStreamRemote;

    public RemoteService(final ConfigServer server, final InputStream jar) {
        final int nodePort = RemoteNodeRunner.safeStart(server, jar);

        this.host = server.host;
        try {
            final Registry registry = LocateRegistry.getRegistry(host, nodePort);
            logbRemote = (LogbRemote) registry.lookup(NodeRunner.NODE_RMI_NAME);
            eventStreamRemote = (EventStreamRemote) registry.lookup(NodeRunner.EVENT_STREAM);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public EventStreamRemote getEventStreamRemote() {
        return eventStreamRemote;
    }

    public FilePiece piece(final LogRequest logRequest) {
        try {
            return logbRemote.getPiece(logRequest);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ListItem> list(final ListRequest request) {
        try {
            List<ListItem> result = logbRemote.list(request);
            for (ListItem item : result) item.host = host;
            return result;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
