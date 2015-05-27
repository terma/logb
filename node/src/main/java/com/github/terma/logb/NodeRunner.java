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
