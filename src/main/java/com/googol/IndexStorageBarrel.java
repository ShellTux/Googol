package com.googol;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndexStorageBarrel extends UnicastRemoteObject implements IndexStorageBarrelI {
  private BlockingQueue<String> urlQueue;
  private HashMap<String, HashSet<String>> indexStore;

  private static Semaphore semaphore = null;

  private static Logger logger = Logger.getLogger("com.googol.IndexStorageBarrel");

  static {
    FileHandler fh;
    try {
      new File("logs/").mkdir();
      fh = new FileHandler("logs/barrel%g.log.xml");

      logger.addHandler(fh);
      logger.setLevel(Level.ALL);
      logger.info("\033[32mIndexStorageBarrels\033[0m Starting...");
    } catch (SecurityException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  };

  protected IndexStorageBarrel() throws RemoteException {
    super();

    urlQueue = new LinkedBlockingQueue<>();
    indexStore = new HashMap<>();
  }

  public static void main(String[] args) throws RemoteException, UnknownHostException, SocketException, KeyNotFoundException {
    System.out.println("--- Welcome to \033[32mIndex Storage Barrels\033[0m ---");

    semaphore = new Semaphore(1);

    final GoogolProperties properties = GoogolProperties.getDefaultSettings();

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

  @Override
  public boolean queueUrl(final String url) throws RemoteException {
    return urlQueue.add(url);
  }

  @Override
  public String unqueueUrl() throws RemoteException {
    try {
      return urlQueue.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RemoteException("Thread interrupted", e);
    }
  }

  @Override
  public String getStatus() throws RemoteException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getStatus'");
  }

  @Override
  public Queue<String> getQueue() throws RemoteException {
    return urlQueue;
  }

  @Override
  public void indexUrl(final String url, final ArrayList<String> words) throws RemoteException {
    if (!indexStore.containsKey(url)) {
      indexStore.put(url, new HashSet<>());
    }

    HashSet<String> urlWords = indexStore.get(url);
    int added = 0;
    for (final String word: words) {
      if (urlWords.contains(word)) {
        continue;
      }

      urlWords.add(word);
      added += 1;
    }

    logger.info(String.format("url %s: %d words added", url, added));
  }
}
