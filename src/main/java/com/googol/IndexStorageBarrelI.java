package com.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Remote interface for the IndexStorageBarrel service,
 * providing methods to queue URLs, retrieve them, and manage indexed data.
 */
public interface IndexStorageBarrelI extends Remote {
  /**
   * Queues a URL for indexing.
   *
   * @param url The URL to be queued.
   * @return true if the URL was successfully queued, false otherwise.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public boolean queueUrl(final String url) throws RemoteException;

  /**
   * Unqueues and returns a URL for processing.
   *
   * @return The next URL to be processed.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public String unqueueUrl() throws RemoteException;

  /**
   * Retrieves the status of the index storage barrel (currently unimplemented).
   *
   * @return A status string.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public String getStatus() throws RemoteException;

  /**
   * Retrieves the current queue of URLs.
   *
   * @return The current queue of URLs.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public Queue<String> getQueue() throws RemoteException;

  /**
   * Indexes a URL with its associated words.
   *
   * @param url The URL to index.
   * @param words The list of words associated with the URL.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public void indexUrl(final String url, final ArrayList<String> words) throws RemoteException;
}
