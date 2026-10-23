package com.ems.utils;

import com.ems.config.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Finds the bundled application (app/index.html) regardless of the IDE's working directory. */
public final class AppLocator {

    private AppLocator() {
    }

    public static Path projectRoot() {
        Path start = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path p = start;
        for (int i = 0; i < 4 && p != null; i++) {
            if (Files.exists(p.resolve("app").resolve("index.html"))) {
                return p;
            }
            p = p.getParent();
        }
        return start;
    }

    public static String url() {
        return url(null);
    }

    public static String url(String query) {
        String custom = ConfigReader.get("app.url", "");
        String base = custom.isEmpty()
                ? projectRoot().resolve("app").resolve("index.html").toUri().toString()
                : custom;
        return (query == null || query.isEmpty()) ? base : base + "?" + query;
    }
}
