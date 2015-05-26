package com.github.terma.logb;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class LogbService implements LogbRemote {

    private final File directory = new File("/Users/terma/Projects/logb");

    @Override
    public FilePiece getPiece(final LogRequest logRequest) throws RemoteException {
        try {
            Logb logb = new Logb(new File(directory, logRequest.name));
            FilePiece piece = logb.getPiece(logRequest.start, logRequest.length);
            logb.close();
            return piece;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ListItem> getLogs(String directory) throws RemoteException {
        return toList(".", new File(directory).listFiles());
    }

    private static List<ListItem> toList(final String path, final File[] files) {
        List<ListItem> result = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(toList(path + "/" + file.getName(), file.listFiles()));
            } else {
                result.add(fileToItem(path, file));
            }
        }
        return result;
    }

    private static ListItem fileToItem(final String path, final File file) {
        ListItem listItem = new ListItem();
        listItem.name = path + "/" + file.getName();
        listItem.length = file.length();
        listItem.lastModified = file.lastModified();
        return listItem;
    }

}
