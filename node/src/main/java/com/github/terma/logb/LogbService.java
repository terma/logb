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
