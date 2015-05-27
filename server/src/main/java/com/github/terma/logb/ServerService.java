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
