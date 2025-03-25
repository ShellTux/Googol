package com.googol;

/**
 * Exception thrown when a requested key is not found in the properties.
 */
public class KeyNotFoundException extends Exception {
    /**
     * Constructs a KeyNotFoundException with the specified key.
     *
     * @param key The key that was not found.
     */
    public KeyNotFoundException(String key) {
        super("Key not found: " + key);
    }
}
