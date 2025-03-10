package com.googol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class GoogolProperties {
  private Optional<Integer> barrelsMulticastPort = Optional.empty();
  private Optional<Integer> barrelsRegistryPort = Optional.empty();
  private Optional<Integer> gatewayRegistryPort = Optional.empty();
  private Optional<Integer> numberBarrels = Optional.empty();
  private Optional<String> barrelsMulticastIP = Optional.empty();

  public static final String propertiesFile = "googol.properties";
  public static GoogolProperties properties = new GoogolProperties(null);
  public static final String[] requiredPropertiesKeys = {
    "gateway_registry_port",
    "barrels_registry_port",
    "barrels_multicast_ip",
    "barrels_multicast_port",
    "number_barrels"
  };

  public GoogolProperties(final String propertiesFile) {
    try {
      FileReader reader = new FileReader(propertiesFile == null ? GoogolProperties.propertiesFile : propertiesFile);
      Properties properties = new Properties();
      properties.load(reader);

      {
        final String grp = properties.getProperty("gateway_registry_port");
        if (grp != null) {
          setGatewayRegistryPort(Integer.parseInt(grp));
        }
      }

      {
        final String brp = properties.getProperty("barrels_registry_port");
        if (brp != null) {
          setBarrelsRegistryPort(Integer.parseInt(brp));
        }
      }

      {
        final String bmi = properties.getProperty("barrels_multicast_ip");
        if (bmi != null) {
          setBarrelsMulticastIP(bmi);
        }
      }

      {
        final String bmp = properties.getProperty("barrels_multicast_port");
        if (bmp != null) {
          setBarrelsMulticastPort(Integer.parseInt(bmp));
        }
      }

      {
        final String nb = properties.getProperty("number_barrels");
        if (nb != null) {
          setNumberBarrels(Integer.parseInt(nb));
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("Failed to read file: " + propertiesFile);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Failed to load properties");
    }
  }

  public static void main(String[] args) {
    GoogolProperties properties = new GoogolProperties(propertiesFile);
  }

  public int getBarrelsMulticastPort() {
    return barrelsMulticastPort.get();
  }

  public int getBarrelsRegistryPort() {
    return barrelsRegistryPort.get();
  }

  public int getGatewayRegistryPort() {
    return gatewayRegistryPort.get();
  }

  public int getNumberBarrels() {
    return numberBarrels.get();
  }

  public String getBarrelsMulticastIP() {
    return barrelsMulticastIP.get();
  }

  public boolean isValid() {
    return barrelsMulticastPort.isPresent()
      && barrelsRegistryPort.isPresent()
      && gatewayRegistryPort.isPresent()
      && numberBarrels.isPresent()
      && barrelsMulticastIP.isPresent();
  }

  public static String getPropertiesfile() {
    return propertiesFile;
  }

  public static GoogolProperties getProperties() {
    return properties;
  }

  private void setBarrelsMulticastPort(int barrelsMulticastPort) {
    this.barrelsMulticastPort = Optional.of(barrelsMulticastPort);
  }

  private void setBarrelsRegistryPort(int barrelsRegistryPort) {
    this.barrelsRegistryPort = Optional.of(barrelsRegistryPort);
  }

  private void setGatewayRegistryPort(int gatewayRegistryPort) {
    this.gatewayRegistryPort = Optional.of(gatewayRegistryPort);
  }

  private void setNumberBarrels(int numberBarrels) {
    this.numberBarrels = Optional.of(numberBarrels);
  }

  private void setBarrelsMulticastIP(String barrelsMulticastIP) {
    this.barrelsMulticastIP = Optional.of(barrelsMulticastIP);
  }

  private static void setProperties(GoogolProperties properties) {
    GoogolProperties.properties = properties;
  }
}
