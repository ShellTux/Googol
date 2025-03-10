package com.googol;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Gateway extends UnicastRemoteObject implements GatewayI {
  private static ArrayList<IndexStorageBarrelI> barrels = new ArrayList<>();

  protected Gateway() throws RemoteException {
    super();
  }

  public static void main(String[] args) throws RemoteException {
    System.out.println("--- Welcome to the gateway ---");

    final int gatewayRegistryPort = GoogolProperties.properties.getGatewayRegistryPort();
    final int barrelsRegistryPort = GoogolProperties.properties.getBarrelsRegistryPort();

    System.out.println("gateway port: " + gatewayRegistryPort);
    System.out.println("barrels port: " + barrelsRegistryPort);

    for (int i = 0; i < GoogolProperties.properties.getNumberBarrels(); ++i) {
      final String barrelName = "barrel" + i;
      IndexStorageBarrelI barrel;
      try {
        System.out.println("Looking up: " + barrelName);
        barrel = (IndexStorageBarrelI) LocateRegistry
          .getRegistry(barrelsRegistryPort)
          .lookup(barrelName);
        barrels.add(barrel);
      } catch (RemoteException | NotBoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    Gateway gateway = new Gateway();
    Registry registry = LocateRegistry.createRegistry(gatewayRegistryPort);
    registry.rebind("Gateway", gateway);
  }
}
