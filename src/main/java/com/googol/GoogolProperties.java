package com.googol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class GoogolProperties {
  private Properties properties;

  private static final String defaultFilePath = "googol.properties";
  private static HashMap<Path, GoogolProperties> loaded;

  static {
    loaded = new HashMap<>();
  }

  public GoogolProperties(final String propertiesFile) {
    try {
      FileReader reader = new FileReader(propertiesFile == null ? defaultFilePath : propertiesFile);
      properties = new Properties();
      properties.load(reader);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("Failed to read file: " + propertiesFile);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Failed to load properties");
    }
  }

  public String getString(final String key) throws KeyNotFoundException {
    if (!properties.containsKey(key)) {
      throw new KeyNotFoundException(key);
    }

    return properties.getProperty(key);
  }

  public int getInt(final String key) throws KeyNotFoundException {
    return Integer.parseInt(getString(key));
  }

  public boolean getBoolean(final String key) throws KeyNotFoundException {
    return Boolean.parseBoolean(getString(key));
  }

  public static GoogolProperties getSettings(final String filePath) {
    final Path path = Paths.get(filePath);

    if (!loaded.containsKey(path)) {
      final GoogolProperties properties = new GoogolProperties(filePath);
      loaded.put(path, properties);
      return properties;
    }

    return loaded.get(path);
  }

  public static GoogolProperties getDefaultSettings() {
    return getSettings(defaultFilePath);
  }

  public static void main(String[] args) {
  }
}
