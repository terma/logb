package com.github.terma.logb;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LogbRemote extends Remote {

    FilePiece getPiece(LogRequest logRequest) throws RemoteException;

    List<ListItem> list(String host, List<String> files) throws RemoteException;
}
