package com.googol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Demonstrates the use of Jsoup for web scraping.
 * It connects to a specified URL and extracts words and links from the page.
 */
public class JsoupDemo {

  /**
   * Main method to execute the Jsoup demonstration.
   *
   * @param args Command-line arguments.
   * @throws IOException if an I/O error occurs during connection to the URL.
   */
  public static void main(String[] args) throws IOException {
    final String url = "http://127.0.0.1:9090/index.html";
    Scanner scanner = new Scanner(System.in);

    List<String> words = new ArrayList<>();
    List<String> links = new ArrayList<>();

    try {
      Document document = Jsoup.connect(url).get();
      StringTokenizer wordsTokenizer = new StringTokenizer(document.text(), " ,.!?:/#%");

      words = Collections
        .list(wordsTokenizer)
        .stream()
        .map(word -> (String) word)
        .collect(Collectors.toList());

      System.out.println(String.format("words = %s", words));

      for (Element linkEl : document.select("a[href]")) {
        final String link = linkEl.attr("abs:href");
        links.add(link);
      }

      System.out.println(String.format("links = %s", links));
    } catch (IOException e) {
      e.printStackTrace();
    }


    System.out.println("Press enter to exit program...");
    scanner.nextLine();
    scanner.close();
  }
}
