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

package com.github.terma.logb.node;

import com.github.terma.logb.StreamEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;

public interface EventStreamRemote extends Remote {

    ArrayList<StreamEvent> get(final EventStreamRequest request, ArrayList<EventStreamPath> paths) throws RemoteException;

    HashSet<String> getTags(ArrayList<EventStreamPath> paths) throws RemoteException;

}
