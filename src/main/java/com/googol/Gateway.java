package com.googol;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class Gateway extends UnicastRemoteObject implements GatewayI {
  private Queue<String> urlQueue;

  private static ArrayList<IndexStorageBarrelI> barrels = new ArrayList<>();

  protected Gateway() throws RemoteException {
    super();

    urlQueue = new LinkedList<>();
  }

  public static void main(String[] args) throws RemoteException, KeyNotFoundException {
    System.out.println("--- Welcome to \033[32mGateway\033[0m ---");

    final GoogolProperties properties = GoogolProperties.getDefaultSettings();

    final int gatewayRegistryPort = properties.getInt("Gateway.Registry.port");
    final int barrelsRegistryPort = properties.getInt("Barrels.Registry.port");
    final int barrelsNumber = properties.getInt("Barrels.number");

    System.out.println("gateway port: " + gatewayRegistryPort);
    System.out.println("barrels port: " + barrelsRegistryPort);

    for (int i = 0; i < barrelsNumber; ++i) {
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
    for (final String url: properties.getString("Gateway.urls").split(",")) {
      if (url.length() == 0) {
        continue;
      }

      gateway.queueUrl(url.strip());
    }

    Registry registry = LocateRegistry.createRegistry(gatewayRegistryPort);
    registry.rebind("gateway", gateway);
  }

  @Override
  public String getStatus() throws RemoteException {
    String status ="\033[5;33mGateway Status\033[0m:\n";

    status += "Queue: ";

    if (urlQueue.isEmpty()) {
      status += "[]";
      return status;
    }

    status += "\n";

    for (final String url: urlQueue) {
      status += String.format("  - %s\n", url);
    }

    return status;
  }

  @Override
  public boolean queueUrl(final String url) throws RemoteException {
    urlQueue.add(url);

    return true;
  }

  @Override
  public ArrayList<String> getTop10Searches() throws RemoteException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTop10Searches'");
  }

@Override
public Optional<String> unqueueUrl() throws RemoteException {
    if (urlQueue.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(urlQueue.remove());
  }
}
