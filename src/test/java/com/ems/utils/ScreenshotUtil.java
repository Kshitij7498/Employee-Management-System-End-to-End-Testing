package com.ems.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    /** Saves a PNG into the run's screenshots folder and returns the file name (or null on failure). */
    public static String capture(WebDriver driver, String testName) {
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String safe = testName.replaceAll("[^A-Za-z0-9_-]", "_");
            String fileName = safe + "_" + new SimpleDateFormat("HHmmss_SSS").format(new Date()) + ".png";
            Path target = ReportPaths.screenshots().resolve(fileName);
            Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
            return null;
        }
    }
}
