package com.googol;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends UnicastRemoteObject {
  private static enum Choice {
    Exit,
    AddUrlToIndex,
    SearchWordInIndex,
    ConsultPagesByUrl,
    Top10,
    Status,
  };

  protected Client() throws RemoteException {
    super();
  }

  public static void main(String[] args) throws RemoteException, NotBoundException, KeyNotFoundException {
    final GoogolProperties properties = GoogolProperties.getDefaultSettings();

    final int gatewayRegistryPort = properties.getInt("Gateway.Registry.port");
    GatewayI gateway = (GatewayI) LocateRegistry
      .getRegistry(gatewayRegistryPort)
      .lookup("gateway");

    final String motd = """
                               _        _ _            _
                              | |      | (_)          | |
  __ _  ___   ___   __ _  ___ | |   ___| |_  ___ _ __ | |_
 / _` |/ _ \\ / _ \\ / _` |/ _ \\| |  / __| | |/ _ \\ '_ \\| __|
| (_| | (_) | (_) | (_| | (_) | | | (__| | |  __/ | | | |_
 \\__, |\\___/ \\___/ \\__, |\\___/|_|  \\___|_|_|\\___|_| |_|\\__|
  __/ |             __/ |
 |___/             |___/
    """;


    Scanner scanner = new Scanner(System.in);

    boolean exit = false;

    while (!exit) {
      System.out.print(motd);

      System.out.println("Select an option:");
      System.out.println("0. Exit");
      System.out.println("1. Add URL for indexing");
      System.out.println("2. Search Googol indexed URLs");
      System.out.println("3. Consult pages associated with a URL");
      System.out.println("4. Top 10 searches");
      System.out.println("5. Status");
      System.out.print("Enter your choice: ");

      final Choice choice = switch (scanner.nextInt()) {
        case 0 -> Choice.Exit;
        case 1 -> Choice.AddUrlToIndex;
        case 2 -> Choice.SearchWordInIndex;
        case 3 -> Choice.ConsultPagesByUrl;
        case 4 -> Choice.Top10;
        case 5 -> Choice.Status;
        default -> Choice.Status;
      };

      System.out.print("\033[H\033[2J");

      try {
        switch (choice) {
          case AddUrlToIndex: {
            System.out.print("Enter url: ");
            final String url = scanner.next();
            gateway.queueUrl(url);
            System.out.println("Adding url: " + url);
          } break;
          case ConsultPagesByUrl: {
            System.out.print("Enter url: ");
            final String url = scanner.next();
            System.out.println("Adding url: " + url);
          } break;
          case Exit: {
            exit = true;
          } break;
          case SearchWordInIndex: {
            System.out.print("Enter word: ");
            final String word = scanner.next();
            System.out.println("Searching word: " + word);
          } break;
          case Status: {
            System.out.println(gateway.getStatus());
          } break;
          case Top10: {
            System.out.println("Top 10");
            final ArrayList<String> top10 = gateway.getTop10Searches();
            for (final String top: top10) {
              System.out.print("  - ");
              System.out.print(top);
              System.out.println();
            }
          } break;
        }
      } catch (RemoteException e) {

      }

      System.out.println("-------------------------------\n");
    }

    scanner.close();
  }
}
