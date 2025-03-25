package com.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Queue;

public interface IndexStorageBarrelI extends Remote {
  public boolean queueUrl(final String url) throws RemoteException;
  public String unqueueUrl() throws RemoteException;
  public String getStatus() throws RemoteException;
  public Queue<String> getQueue() throws RemoteException;
  public void indexUrl(final String url, final ArrayList<String> words) throws RemoteException;
}
