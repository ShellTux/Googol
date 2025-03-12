package com.googol;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class IndexStorageBarrel extends UnicastRemoteObject implements IndexStorageBarrelI {
  private static Semaphore semaphore = null;

  protected IndexStorageBarrel() throws RemoteException {
    super();
  }

  public static void main(String[] args) throws RemoteException, UnknownHostException, SocketException, KeyNotFoundException {
    System.out.println("--- Welcome to \033[32mIndex Storage Barrels\033[0m ---");

    semaphore = new Semaphore(1);

    final GoogolProperties properties = GoogolProperties.getDefaultSettings();

    System.out.println("properties: " + properties);

    final int barrelsRegistryPort = properties.getInt("Barrels.Registry.port");
    final int barrelsNumber = properties.getInt("Barrels.number");

    System.out.println("barrels port: " + barrelsRegistryPort);
    System.out.println("number of barrels: " + barrelsNumber);

    System.getProperties().put("java.security.policy", "policy.all");

    Registry registry = LocateRegistry.createRegistry(barrelsRegistryPort);

    ArrayList<IndexStorageBarrelI> barrels = new ArrayList<>();
    for (int i = 0; i < barrelsNumber; ++i) {
      IndexStorageBarrel barrel = new IndexStorageBarrel();
      final String barrelName = "barrel" + i;

      barrels.add(barrel);
      registry.rebind(barrelName, barrel);
      System.out.println("rebind: " + barrelName);
    }
  }
}
