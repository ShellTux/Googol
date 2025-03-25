package com.googol;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * Represents the client that interacts with the gateway for
 * URL indexing and searching functionality.
 */
public class Client extends UnicastRemoteObject {
  private static GatewayI gateway;
  private static int gatewayRegistryPort;

  /**
   * Enum representing the various choices available to the user.
   */
  private static enum Choice {
    Exit,
    AddUrlToIndex,
    SearchWordInIndex,
    ConsultPagesByUrl,
    Top10,
    Status,
  }

  protected Client() throws RemoteException {
    super();
  }

  /**
   * Connects to the gateway service.
   */
  private static void connectToGateway() {
    try {
      gateway = (GatewayI) LocateRegistry
        .getRegistry(gatewayRegistryPort)
        .lookup("gateway");
    } catch (RemoteException | NotBoundException e) {
      // TODO Auto-generated catch block
      gateway = null;
    }
  }

  /**
   * Main method to execute the client application.
   *
   * @param args Command-line arguments.
   * @throws RemoteException if a communication-related exception occurs.
   * @throws NotBoundException if the RMI registry does not contain a binding for the gateway.
   */
  public static void main(String[] args) throws RemoteException, NotBoundException, KeyNotFoundException {
    final GoogolProperties properties = GoogolProperties.getDefaultSettings();

    gatewayRegistryPort = properties.getInt("Gateway.Registry.port");

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

      if (gateway == null) {
        connectToGateway();
      }

      if (gateway == null) {
        System.out.print("\033[H\033[2J");
        System.out.println("Failed connecting to gateway...");
        System.out.println("-------------------------------\n");
        continue;
      }

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
            String userInput = JOptionPane.showInputDialog(null, "Enter your terms: ", "Googol", JOptionPane.QUESTION_MESSAGE);
            if (userInput == null) {
              break;
            }

            final String[] words = userInput.split(" ");

            //System.out.print("Enter word: ");
            //final String[] words = scanner.nextLine().split(" ");

            System.out.println(String.format("Searching words: %s", Arrays.asList(words)));
            final HashSet<Url> urls = gateway.searchPagesByWords(words);
            System.out.println(String.format("urls: %s", urls));
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
        System.out.println("Failed connecting to gateway...");
        gateway = null;
      }

      System.out.println("-------------------------------\n");
    }

    scanner.close();
  }
}
