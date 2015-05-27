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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeRunner {

    private static final Logger LOGGER = Logger.getLogger(NodeRunner.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting node...");
        try {
            LogbRemote logbService = (LogbRemote) UnicastRemoteObject.exportObject(new LogbService(), 0);
            Registry registry = LocateRegistry.createRegistry(1200);
            registry.rebind("logb", logbService);
            LOGGER.info("Node sucessefully started");
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Can't start node", e);
            System.exit(1);
        }
    }

}
