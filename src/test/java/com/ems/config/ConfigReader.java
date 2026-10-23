package com.ems.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads config.properties; any value can be overridden with -Dkey=value. */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String fromSystem = System.getProperty(key);
        return fromSystem != null ? fromSystem : PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
