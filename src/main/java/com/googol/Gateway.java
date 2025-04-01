package com.googol;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Gateway class that acts as a middleware between the {@link Client} and
 * {@link IndexStorageBarrel}.
 */
public class Gateway extends UnicastRemoteObject implements GatewayI {
  private static ArrayList<IndexStorageBarrelI> barrels = new ArrayList<>();

  private static Logger logger = Logger.getLogger("com.googol.Gateway");

  static {
    FileHandler fh;
    try {
      new File("logs/").mkdir();
      fh = new FileHandler("logs/gateway%g.log.xml");

      logger.addHandler(fh);
      logger.setLevel(Level.ALL);
      logger.info("\033[32mGateway\033[0m Starting...");
    } catch (SecurityException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  };

  protected Gateway() throws RemoteException {
    super();
  }

  /**
   * Main method to start the gateway service.
   *
   * @param args Command-line arguments.
   * @throws RemoteException if a communication-related exception occurs.
   * @throws KeyNotFoundException if a required key in the properties is not found.
   */
  public static void main(String[] args) throws RemoteException, KeyNotFoundException {
    System.out.println("--- Welcome to \033[32mGateway\033[0m ---");

    final GoogolProperties properties = GoogolProperties.getDefaultSettings();

    final String gatewayRegistryHost = properties.getString("Gateway.Registry.host");
    final int gatewayRegistryPort = properties.getInt("Gateway.Registry.port");
    final String barrelsRegistryHost = properties.getString("Barrels.Registry.host");
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
          .getRegistry(barrelsRegistryHost, barrelsRegistryPort)
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

  /**
   * Returns the current status of the gateway including the status of
   * the URL queue.
   *
   * @return Status summary of the gateway.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public String getStatus() throws RemoteException {
    logger.info(String.format("Status requested!"));

    String status ="\033[5;33mGateway Status\033[0m:\n";

    final Queue<String> urlQueue = getBarrel().getQueue();

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

  /**
   * Retrieves the top 10 searched URLs.
   *
   * @return A list of top 10 searched URLs.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public ArrayList<String> getTop10Searches() throws RemoteException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTop10Searches'");
  }

  private IndexStorageBarrelI getBarrel() throws RemoteException {
    return barrels.get(0);
  }

  /**
   * Queues a URL for indexing.
   *
   * @param url The URL to be queued.
   * @return true if the URL was successfully queued, false otherwise.
   * @throws RemoteException if a communication-related exception occurs.
   */
  @Override
  public boolean queueUrl(String url) throws RemoteException {
    return getBarrel().queueUrl(url);
  }

  @Override
  public HashSet<Url> searchPagesByWords(final String[] words) throws RemoteException {
    return getBarrel().searchPagesByWords(words);
  }
}
