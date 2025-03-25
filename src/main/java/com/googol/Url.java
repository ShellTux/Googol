package com.googol;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;

public class Url {
  private String url;
  private LocalDateTime lastIndexedTime;
  private long reindexIntervalSeconds;

  private static long defaultReindexIntervalSeconds = 24 * 60 * 60;

  private static boolean isDebugEnabled = false;

  public Url(final String url) {
    this.url = url;
    lastIndexedTime = null;
    reindexIntervalSeconds = defaultReindexIntervalSeconds;
  }

  public Url(final String url, final long reindexInterval) {
    this(url);

    this.reindexIntervalSeconds = reindexInterval;
  }

  public String getUrl() {
    return url;
  }

  public LocalDateTime getLastIndexedTime() {
    return lastIndexedTime;
  }

  public boolean canReindex() {
    if (lastIndexedTime == null) {
      return true;
    }

    LocalDateTime nextIndexingTime = lastIndexedTime.plusSeconds(reindexIntervalSeconds);
    return LocalDateTime.now().isAfter(nextIndexingTime);
  }

  public void setIndex() {
    if (!canReindex()) {
      if (isDebugEnabled) {
        System.out.println(String.format("Cannot reindex website: %s yet. Last indexed at: %s", url, lastIndexedTime));
      }
      return;
    }

    lastIndexedTime = LocalDateTime.now();
    if (isDebugEnabled) {
      System.out.println(String.format("Indexed website: %s at %s", url, lastIndexedTime));
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Url)) {
      return false;
    }

    Url other = (Url) obj;
    return Objects.equals(url, other.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  public static void main(String[] args) throws InterruptedException {
    final int interval = 1;

    Url url = new Url("https://google.pt", interval);

    // First indexing attempt
    url.setIndex();

    // Attempt to index again immediately
    url.setIndex();

    // Wait (longer than the reindex interval)
    System.out.println(String.format("Waiting for %d seconds to allow reindexing...", interval + 1));
    Thread.sleep((interval + 1) * 1000);

    // Second indexing attempt after waiting
    url.setIndex(); // Should be able to index again


    // Example usage
    HashSet<Url> urlSet = new HashSet<>();

    Url url1 = new Url("https://google.pt", 5);
    urlSet.add(url1);

    Url url2 = new Url("https://google.pt");
    System.out.println(String.format("Contains %s = %b", url2, urlSet.contains(url2)));

    Url url3 = new Url("https://example.com");
    urlSet.add(url3);
    System.out.println(String.format("Contains %s = %b", url3, urlSet.contains(url3)));

    Scanner scanner = new Scanner(System.in);

    scanner.nextLine();
    scanner.close();
  }
}
