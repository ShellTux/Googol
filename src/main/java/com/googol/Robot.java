package search;

import java.rmi.registry.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Robot {
    public static void main(String[] args) {
        try {

            Index index = (Index) LocateRegistry.getRegistry(8183).lookup("index");
            while (true) {
                try {
                    final int seconds = 3;
                    System.out.printf("Sleeping %d seconds...\n", seconds);
                    Thread.sleep(1000 * seconds);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                String url = index.takeNext();
                if (url == null) {
                    continue;
                }
                System.out.println("Parsing: " + url);
                Document doc = Jsoup.connect(url).get();
                //System.out.println(doc);
                //Todo: Read JSOUP documentation and parse the html to index the keywords. 
                //Then send back to server via index.addToIndex(...)
                StringTokenizer words = new StringTokenizer(doc.text(), " ,.!?:/#%");
                for (String word = words.nextToken(); words.hasMoreElements(); word = words.nextToken()) {
                    if (word.equals("animal")) {
                        System.out.println("word = " + word);
                    }
                    index.addToIndex(word.toLowerCase(), url);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
