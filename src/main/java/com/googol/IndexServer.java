package search;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public class IndexServer extends UnicastRemoteObject implements Index {
    private Queue<String> urlsToIndex;
    private Set<String> urlsIndexed;

    private HashMap<String, ArrayList<String>> word2url;
    private boolean debug;

    public IndexServer() throws RemoteException {
        super();
        //This structure has a number of problems. The first is that it is fixed size. Can you enumerate the others?
        urlsToIndex = new LinkedList<>();
        urlsIndexed = new HashSet<>();
        word2url = new HashMap<>();
        debug = true;
    }

    public static void main(String args[]) {
        try {
            IndexServer server = new IndexServer();
            Registry registry = LocateRegistry.createRegistry(8183);
            registry.rebind("index", server);
            System.out.println("Server ready. Waiting for input...");

            //TODO: This approach needs to become interactive. Use a Scanner(System.in) to create a rudimentary user interface to:
            //1. Add urls for indexing
            //2. search indexed urls
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Add URL for indexing");
                System.out.println("2. Search indexed URLs");
                System.out.println("3. Exit");
                final int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        //System.out.print("Enter URL: ");
                        //String url = scanner.next();
                        //server.putNew(url);
                        //server.putNew("https://pt.wikipedia.org/wiki/Wikip%C3%A9dia:P%C3%A1gina_principal");
                        //server.putNew("https://en.wikipedia.org/wiki/Lion");
                        server.putNew("https://en.wikipedia.org/wiki/Cheetah");
                        //server.putNew("http://127.0.0.1:8080/Lion.html");
                        //server.putNew("http://127.0.0.1:8080/Tiger.html");
                        //server.putNew("http://127.0.0.1:8080/Cheetah.html");
                        break;
                    case 2:
                        System.out.print("Enter word to search: ");
                        String word = scanner.next();
                        ArrayList<String> results = server.searchWord(word);
                        System.out.println("Results: " + results);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private long counter = 0, timestamp = System.currentTimeMillis();;

    public String takeNext() throws RemoteException {
        //TODO: not implemented fully. Prefer structures that return in a push/pop fashion
        return urlsToIndex.isEmpty() ? null : urlsToIndex.remove();
    }

    public void putNew(String url) throws java.rmi.RemoteException {
        //TODO: Example code. Must be changed to use structures that have primitives such as .add(...)
        if (urlsIndexed.contains(url)) {
            return;
        }

        urlsToIndex.add(url);
        if (debug) {
            System.out.println("Queued: " + url);
        }
    }

    public void addToIndex(String word, String url) throws java.rmi.RemoteException {
        //TODO: not implemented
        word = word.toLowerCase();
        if (!word2url.containsKey(word)) {
            ArrayList<String> urls = new ArrayList<>();
            urls.add(url);
            word2url.put(word, urls);
            return;
        }

        word2url.get(word).add(url);
    }


    public ArrayList<String> searchWord(String word) throws java.rmi.RemoteException {
        //TODO: not implemented
        word2url.forEach((indexWord, urls) -> {
            if (!debug) {
                return;
            }

            //System.out.println("word2url word = " + indexWord);
        });

        word = word.toLowerCase();

        return word2url.containsKey(word) ? null : word2url.get(word);
    }
}
