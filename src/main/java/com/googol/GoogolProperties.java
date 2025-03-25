package com.googol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

/**
 * Manages application properties via loading and accessing configuration settings.
 */
public class GoogolProperties {
  private Properties properties;

  private static final String defaultFilePath = "googol.properties";
  private static HashMap<Path, GoogolProperties> loaded;

  static {
    loaded = new HashMap<>();
  }

  /**
   * Constructs a GoogolProperties instance by loading properties from a specified file.
   *
   * @param propertiesFile The path to the properties file.
   */
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

  /**
   * Retrieves a string value associated with the specified key.
   *
   * @param key The key of the desired property.
   * @return The value associated with the specified key.
   * @throws KeyNotFoundException if the key does not exist.
   */
  public String getString(final String key) throws KeyNotFoundException {
    if (!properties.containsKey(key)) {
      throw new KeyNotFoundException(key);
    }

    return properties.getProperty(key);
  }

  /**
   * Retrieves an integer value associated with the specified key.
   *
   * @param key The key of the desired property.
   * @return The integer value associated with the specified key.
   * @throws KeyNotFoundException if the key does not exist.
   */
  public int getInt(final String key) throws KeyNotFoundException {
    return Integer.parseInt(getString(key));
  }

  /**
   * Retrieves a boolean value associated with the specified key.
   *
   * @param key The key of the desired property.
   * @return The boolean value associated with the specified key.
   * @throws KeyNotFoundException if the key does not exist.
   */
  public boolean getBoolean(final String key) throws KeyNotFoundException {
    return Boolean.parseBoolean(getString(key));
  }

  /**
   * Retrieves the properties from a specified file path.
   *
   * @param filePath The path of the properties file.
   * @return A GoogolProperties instance containing the loaded properties.
   */
  public static GoogolProperties getSettings(final String filePath) {
    final Path path = Paths.get(filePath);

    if (!loaded.containsKey(path)) {
      final GoogolProperties properties = new GoogolProperties(filePath);
      loaded.put(path, properties);
      return properties;
    }

    return loaded.get(path);
  }

  /**
   * Retrieves default settings from the default properties file.
   *
   * @return A GoogolProperties instance populated with default settings.
   */
  public static GoogolProperties getDefaultSettings() {
    return getSettings(defaultFilePath);
  }

  /**
   * Main method (currently not in use).
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
  }
}
