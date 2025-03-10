package com.googol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
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

  public static void main(String[] args) throws RemoteException, UnknownHostException, SocketException {
    semaphore = new Semaphore(1);


    final String multicastIP = GoogolProperties.properties.getBarrelsMulticastIP();
    final int multicastPort = GoogolProperties.properties.getBarrelsMulticastPort();
    final int barrelsRegistryPort = GoogolProperties.properties.getBarrelsRegistryPort();
    final int nBarrels = GoogolProperties.properties.getNumberBarrels();

    System.out.println("multicast ip: " + multicastIP);
    System.out.println("multicast port: " + multicastPort);
    System.out.println("barrels port: " + barrelsRegistryPort);
    System.out.println("number of barrels: " + nBarrels);

    try {
      MulticastSocket skt = new MulticastSocket(multicastPort);
      InetAddress mCastAddr = InetAddress.getByName(multicastIP);
      skt.joinGroup(new InetSocketAddress(mCastAddr, multicastPort), NetworkInterface.getByIndex(0));
    } catch (IOException e) {
      // TODO: Auto-generated
      e.printStackTrace();
    }

    System.getProperties().put("java.security.policy", "policy.all");

    Registry registry = LocateRegistry.createRegistry(barrelsRegistryPort);

    ArrayList<IndexStorageBarrelI> barrels = new ArrayList<>();
    for (int i = 0; i < nBarrels; ++i) {
      IndexStorageBarrel barrel = new IndexStorageBarrel();
      final String barrelName = "barrel" + i;

      barrels.add(barrel);
      registry.rebind(barrelName, barrel);
      System.out.println("rebind: " + barrelName);
    }
  }
}
