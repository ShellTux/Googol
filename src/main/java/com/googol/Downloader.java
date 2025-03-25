package com.googol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Downloader implements Runnable {
    private ArrayList<IndexStorageBarrelI> barrels;
    private boolean running;
    private int id;
    private String name;
    private Thread thread;

    private static Logger logger = Logger.getLogger("com.googol.Downloader");
    private static Set<String> stopWords = new HashSet<>();

    static {
        FileHandler fh;
        try {
            new File("logs/").mkdir();
            fh = new FileHandler("logs/downloader%g.log.xml");

            loadStopWords();

            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            logger.info("\033[32mDownloader\033[0m Starting...");
        } catch (SecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    };

    public static void loadStopWords() {
        stopWords = new HashSet<>();
        // TODO: move hardcoded string to config
        try (BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt"))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String word = line.strip();
                System.out.println("Loaded stop word: " + word);
                stopWords.add(word);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException, KeyNotFoundException {
        final GoogolProperties properties = GoogolProperties.getDefaultSettings();

        final int barrelsRegistryPort = properties.getInt("Barrels.Registry.port");
        final int downloadersThreads = properties.getInt("Downloaders.threads");
        final int numberBarrels = properties.getInt("Barrels.number");

        // TODO: Send using reliable multicast
        ArrayList<IndexStorageBarrelI> barrels = new ArrayList<>();

        for (int i = 0; i < numberBarrels; ++i) {
            final String barrelName = "barrel" + i;

            try {
                final IndexStorageBarrelI barrel = (IndexStorageBarrelI) LocateRegistry
                    .getRegistry(barrelsRegistryPort)
                    .lookup(barrelName);
                barrels.add(barrel);
                logger.info(String.format("Found \033[36m%s\033[0m\n", barrelName));
            } catch (RemoteException | NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        ArrayList<Downloader> downloaders = new ArrayList<>();

        for (int i = 0; i < downloadersThreads; ++i) {
            final Downloader downloader = new Downloader(i, barrels);

            downloaders.add(downloader);
        }

        downloaders.get(0).start();
    }

    /**
     * @param id
     * @param barrels
     * @deprecated TODO: Remove this constructor for the reliable multicast
     * @throws RemoteException
     */
    public Downloader(final int id, final ArrayList<IndexStorageBarrelI> barrels) throws RemoteException {
        super();

        running = false;
        this.barrels = barrels;
        this.id = id;
        this.name = String.format("downloader%d", id);
    }

    void start() {
        if (running) {
            return;
        }

        thread = new Thread(this);
        thread.setName("thread_" + name);
        thread.start();
    }

    void stop() {
        running = false;
        if (thread != null) {
            logger.info(String.format("Downloader %s stopping... Waiting for processing thread to finish.", thread.getName()));
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.info(String.format("Downloader %s stopped", thread.getName()));
    }

    private IndexStorageBarrelI getBarrel() {
        return barrels.get(0);
    }

    private String takeUrl() throws RemoteException {
        return getBarrel().unqueueUrl();
    }

    @Override
    public void run() {
        running = true;
        logger.info(String.format("Starting run on downloader \033[36m%s\033[0m", name));
        while (running) {
            try {
                final String url = takeUrl();
                logger.info(String.format("Downloader %s, indexing %s", name, url));
                final ArrayList<String> words = indexUrl(url);
                getBarrel().indexUrl(url, words);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        logger.info(String.format("Downloader %s stopped.", name));
    }

    private ArrayList<String> indexUrl(final String url) {
        List<String> urlWords = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        try {
            Document document = Jsoup.connect(url).get();

            urlWords = Collections
                .list(new StringTokenizer(document.text(), " ,.!?:/#%"))
                .stream()
                .map(word -> (String)word)
                .collect(Collectors.toList());
            System.out.println(String.format("words = %s", urlWords));

            IndexStorageBarrelI barrel = getBarrel();
            for (Element linkEl : document.select("a[href]")) {
                final String link = linkEl.attr("abs:href");
                links.add(link);
                barrel.queueUrl(link);
            }
            System.out.println(links);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<String>(urlWords);
    }

}
