package com.ems.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.ems.config.ConfigReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Builds the HTML execution report (ExtentReports). */
public final class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(ReportPaths.executionReport().toString());
            spark.config().setDocumentTitle("EMS End-to-End Test Report");
            spark.config().setReportName("Employee Management System - Execution Report");
            spark.config().setTheme(Theme.STANDARD);
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "Employee Management System");
            extent.setSystemInfo("Browser", ConfigReader.get("browser", "chrome"));
            extent.setSystemInfo("Headless", String.valueOf(ConfigReader.getBoolean("headless", false)));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extent;
    }

    public static ExtentTest startTest(String name, String description) {
        ExtentTest test = getInstance().createTest(name, description);
        CURRENT.set(test);
        return test;
    }

    public static ExtentTest current() {
        return CURRENT.get();
    }

    public static void info(String message) {
        ExtentTest test = CURRENT.get();
        if (test != null) {
            test.info(message);
        }
    }

    public static synchronized void flush() {
        if (extent == null) {
            return;
        }
        extent.flush();
        try {
            // reports/latest-report.html always redirects to the newest run.
            Path redirect = ReportPaths.root().resolve("latest-report.html");
            String target = ReportPaths.runDir().getFileName() + "/ExecutionReport.html";
            Files.write(redirect, ("<html><head><meta http-equiv=\"refresh\" content=\"0; url="
                    + target + "\"></head><body><a href=\"" + target + "\">Open latest report</a></body></html>")
                    .getBytes("UTF-8"));
        } catch (IOException e) {
            System.err.println("Could not write latest-report.html: " + e.getMessage());
        }
        System.out.println("\n=== Execution report: " + ReportPaths.executionReport() + " ===");
        System.out.println("=== Defect reports:   " + ReportPaths.defects() + " ===\n");
    }
}
