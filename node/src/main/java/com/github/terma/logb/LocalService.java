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

import com.github.terma.logb.node.EventStreamNode;
import com.github.terma.logb.node.EventStreamRemote;

import java.rmi.RemoteException;
import java.util.List;

public class LocalService {

    public EventStreamRemote getEventStreamRemote() {
        return new EventStreamNode();
    }

    public List<ListItem> list(final ListRequest request) {
        try {
            return new LogbService().list(request);
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
