package com.googol;

public class KeyNotFoundException extends Exception {
    public KeyNotFoundException(String key) {
        super("Key not found: " + key);
    }
}
