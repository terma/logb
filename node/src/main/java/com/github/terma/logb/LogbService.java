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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LogbService implements LogbRemote {

    private static final int LIST_MAX_SIZE = 100;

    @Override
    public FilePiece getPiece(final LogRequest logRequest) throws RemoteException {
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
    public List<ListItem> list(final ListRequest request) throws RemoteException {
        List<ListItem> result = new ArrayList<>();
        for (String file : request.files) toList(request, new File(file).listFiles(), result);
        Collections.sort(result, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return Long.compare(o2.lastModified, o1.lastModified);
            }
        });
        if (result.size() > LIST_MAX_SIZE) result = result.subList(0, LIST_MAX_SIZE);
        return result;
    }

    private static void toList(final ListRequest request, final File[] files, final List<ListItem> result) {
        for (final File file : files) {
            if (file.isFile()) {
                if (checkFileName(request, file) && checkContent(request, file)) result.add(fileToItem(null, file));
            } else {
                toList(request, file.listFiles(), result);
            }
        }
    }

    private static boolean checkFileName(final ListRequest request, final File file) {
        return request.fileName == null
                || file.getPath().toLowerCase().contains(request.fileName.toLowerCase());
    }

    private static boolean checkContent(final ListRequest request, final File file) {
        if (request.content == null) return true;
        if (file.length() > 1024 * 1024) return false; // no more 1Mb for checking

        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(request.content)) return true;
            }
            return false;
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
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
