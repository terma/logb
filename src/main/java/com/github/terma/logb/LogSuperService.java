package com.github.terma.logb;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class LogSuperService implements LogbRemote {

    private LogbRemote logbRemote;

    public LogSuperService() {
        try {
            Registry registry = LocateRegistry.getRegistry(1200);
            logbRemote = (LogbRemote) registry.lookup("logb");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FilePiece getPiece(LogRequest logRequest) throws RemoteException {
        return logbRemote.getPiece(logRequest);
    }

    @Override
    public List<ListItem> getLogs(String directory) throws RemoteException {
        return logbRemote.getLogs(directory);
    }

}
