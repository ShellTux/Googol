package com.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Optional;

public interface GatewayI extends Remote {
  public String getStatus() throws RemoteException;
  public boolean queueUrl(final String url) throws RemoteException;
  public Optional<String> unqueueUrl() throws RemoteException;
  public ArrayList<String> getTop10Searches() throws RemoteException;
}
