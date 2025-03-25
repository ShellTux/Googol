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

/**
 * IndexStorageBarrel acts as a storage system for URLs and their indexed words.
 * It handles queuing and indexing requests from downloaders.
 */
public class IndexStorageBarrel extends UnicastRemoteObject implements IndexStorageBarrelI {
  private BlockingQueue<String> urlQueue;
  private HashMap<String, HashSet<Url>> indexStoreTerm2Urls;
  private HashMap<Url, HashSet<String>> indexStoreUrl2Terms;
  private HashSet<Url> indexedUrls;

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
    indexStoreTerm2Urls = new HashMap<>();
    indexStoreUrl2Terms = new HashMap<>();
    indexedUrls = new HashSet<>();
  }

  /**
   * Main method to initialize and start the index storage barrels.
   *
   * @param args Command-line arguments.
   * @throws RemoteException if a communication-related exception occurs.
   * @throws UnknownHostException if the local host name cannot be resolved.
   * @throws SocketException if an error occurs while creating a socket.
   * @throws KeyNotFoundException if a required key in the properties is not found.
   */
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

  /**
   * Queues a URL for indexing.
   *
   * @param url The URL to be queued.
   * @return true if the URL was successfully queued, false otherwise.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public boolean queueUrl(final String url) throws RemoteException {
    // TODO: add url to queue if it was index a long time ago
    if (indexedUrls.contains(new Url(url))) {
      return false;
    }

    if (urlQueue.contains(url)) {
      return false;
    }

    return urlQueue.add(url);
  }

  /**
   * Unqueues and returns a URL for processing.
   *
   * @return The next URL to be processed.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public String unqueueUrl() throws RemoteException {
    try {
      return urlQueue.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RemoteException("Thread interrupted", e);
    }
  }

  /**
   * Gets the status of the IndexStorageBarrel (currently unimplemented).
   *
   * @return A status string.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public String getStatus() throws RemoteException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getStatus'");
  }

  /**
   * Retrieves the current queue of URLs.
   *
   * @return The current queue of URLs.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public Queue<String> getQueue() throws RemoteException {
    return urlQueue;
  }

  /**
   * Indexes a URL with its associated words into the storage.
   *
   * @param url The URL to index.
   * @param words The list of words associated with the URL.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public void indexUrl(final String url, final ArrayList<String> words) throws RemoteException {
    final Url indexedUrl = new Url(url);
    int addedWords = 0;

    if (!indexStoreUrl2Terms.containsKey(indexedUrl)) {
      indexStoreUrl2Terms.put(indexedUrl, new HashSet<>());
    }

    HashSet<String> terms = indexStoreUrl2Terms.get(indexedUrl);
    terms.add(url);

    indexStoreUrl2Terms.replace(indexedUrl, terms);

    for (final String word: words) {
      if (!indexStoreTerm2Urls.containsKey(word)) {
        indexStoreTerm2Urls.put(word, new HashSet<>());
      }

      HashSet<Url> urls = indexStoreTerm2Urls.get(word);

      if (urls.add(indexedUrl)) {
        addedWords += 1;
      }


      indexStoreTerm2Urls.replace(word, urls);
    }

    indexedUrls.add(indexedUrl);

    logger.info(String.format("url %s: %d words added", url, addedWords));
  }

  @Override
  public HashSet<Url> searchPagesByWords(final String[] words) throws RemoteException {
    HashSet<Url> resultUrls = new HashSet<>();

    for (final String word : words) {
        HashSet<Url> urls = indexStoreTerm2Urls.get(word);
        if (urls != null) {
            resultUrls.addAll(urls);
        }
    }

    return resultUrls;
  }
}
