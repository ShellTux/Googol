package com.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Remote interface for the Gateway service to provide URL indexing and searching functionality.
 */
public interface GatewayI extends Remote {
  /**
   * Gets the current status of the gateway.
   *
   * @return A string representing the current status.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public String getStatus() throws RemoteException;

  /**
   * Queues a URL for indexing.
   *
   * @param url The URL to be queued.
   * @return true if the URL was successfully queued, false otherwise.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public boolean queueUrl(final String url) throws RemoteException;

  /**
   * Retrieves the top 10 searched URLs.
   *
   * @return A list of top 10 searched URLs.
   * @throws RemoteException if a communication-related exception occurs.
   */
  public ArrayList<String> getTop10Searches() throws RemoteException;

  public HashSet<Url> searchPagesByWords(final String[] words) throws RemoteException;
}
