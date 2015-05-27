package com.github.terma.logb;

import java.rmi.RemoteException;
import java.util.List;

public class LocalService {

    public List<ListItem> list(final List<String> files) {
        try {
            return new LogbService().list(null, files);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public FilePiece piece(LogRequest request) {
        try {
            return new LogbService().getPiece(request);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
