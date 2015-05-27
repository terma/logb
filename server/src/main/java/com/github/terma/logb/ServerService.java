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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ServerService {

    private final String host;
    private final LogbRemote logbRemote;

    public ServerService(final String host) {
        this.host = host;
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1200);
            logbRemote = (LogbRemote) registry.lookup("logb");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public FilePiece piece(LogRequest logRequest) {
        try {
            return logbRemote.getPiece(logRequest);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ListItem> list(List<String> files) {
        try {
            return logbRemote.list(host, files);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
