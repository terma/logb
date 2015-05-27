package com.github.terma.logb;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class LogbService implements LogbRemote {

    @Override
    public FilePiece getPiece(LogRequest logRequest) throws RemoteException {
        try {
            Logb logb = new Logb(logRequest.file);
            FilePiece piece = logb.getPiece(logRequest.start, logRequest.length);
            logb.close();
            return piece;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ListItem> list(final String host, List<String> files) throws RemoteException {
        List<ListItem> result = new ArrayList<>();
        for (String file : files) result.addAll(toList(host, new File(file).listFiles()));
        return result;
    }

    private static List<ListItem> toList(final String host, final File[] files) {
        final List<ListItem> result = new ArrayList<>();
        for (final File file : files) {
            if (file.isDirectory()) {
                result.addAll(toList(host, file.listFiles()));
            } else {
                result.add(fileToItem(host, file));
            }
        }
        return result;
    }

    private static ListItem fileToItem(final String host, final File file) {
        ListItem listItem = new ListItem();
        listItem.host = host;
        listItem.file = file.getAbsolutePath();
        listItem.length = file.length();
        listItem.lastModified = file.lastModified();
        return listItem;
    }

}
