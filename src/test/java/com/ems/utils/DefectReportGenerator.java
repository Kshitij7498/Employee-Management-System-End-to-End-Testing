package com.ems.utils;

import com.ems.config.ConfigReader;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates a Jira-style defect report for every failed test:
 *   defects/DEF-001_<test>.md        human-readable defect (copy/paste into Jira)
 *   defects/jira-import.csv          one row per defect (Jira CSV import)
 */
public final class DefectReportGenerator {

    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static final Object CSV_LOCK = new Object();

    private DefectReportGenerator() {
    }

    public static String generate(ITestResult result, String screenshot, List<String> steps) {
        String defectId = String.format("DEF-%03d", SEQ.incrementAndGet());
        String method = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String title = (description == null || description.isEmpty()) ? method : description;
        List<String> groups = Arrays.asList(result.getMethod().getGroups());
        String[] severity = classify(groups);
        String module = result.getTestClass().getRealClass().getSimpleName().replace("Tests", "");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Throwable error = result.getThrowable();
        String message = error == null ? "Unknown failure" : String.valueOf(error.getMessage()).trim();
        String browser = ConfigReader.get("browser", "chrome")
                + (ConfigReader.getBoolean("headless", false) ? " (headless)" : "");
        String summary = "[" + module + "] " + title + " - test failed";

        StringBuilder md = new StringBuilder();
        md.append("# ").append(defectId).append(": ").append(summary).append("\n\n");
        md.append("| Field | Value |\n|---|---|\n");
        md.append("| Project | EMS (Employee Management System) |\n");
        md.append("| Issue Type | Bug |\n");
        md.append("| Status | Open |\n");
        md.append("| Priority | ").append(severity[0]).append(" |\n");
        md.append("| Severity | ").append(severity[1]).append(" |\n");
        md.append("| Component | ").append(module).append(" |\n");
        md.append("| Labels | automation, ").append(String.join(", ", groups)).append(" |\n");
        md.append("| Reporter | Automation Framework (Selenium/TestNG) |\n");
        md.append("| Detected On | ").append(timestamp).append(" |\n");
        md.append("| Environment | ").append(browser).append(" / ").append(System.getProperty("os.name"))
                .append(" / Java ").append(System.getProperty("java.version")).append(" |\n");
        md.append("| Test Reference | `").append(result.getTestClass().getName()).append("#").append(method).append("` |\n\n");

        md.append("## Description\n").append(title).append("\n\n");
        md.append("## Steps to Reproduce\n");
        if (steps.isEmpty()) {
            md.append("1. Open the application\n");
        }
        for (int i = 0; i < steps.size(); i++) {
            md.append(i + 1).append(". ").append(steps.get(i)).append("\n");
        }
        md.append("\n## Expected Result\nApplication behaves as described in: ").append(title).append("\n\n");
        md.append("## Actual Result\n```\n").append(message).append("\n```\n\n");
        if (screenshot != null) {
            md.append("## Attachment\n![Failure screenshot](../screenshots/").append(screenshot).append(")\n\n");
        }
        md.append("## Stack Trace (top frames)\n```\n").append(topFrames(error, 12)).append("\n```\n");

        try {
            Path file = ReportPaths.defects().resolve(defectId + "_" + method.replaceAll("[^A-Za-z0-9_-]", "_") + ".md");
            Files.write(file, md.toString().getBytes(StandardCharsets.UTF_8));
            appendCsv(defectId, summary, severity[0], module, groups, steps, message, screenshot, browser);
        } catch (IOException e) {
            System.err.println("Could not write defect report: " + e.getMessage());
        }
        return defectId;
    }

    private static String[] classify(List<String> groups) {
        if (groups.contains("smoke")) {
            return new String[]{"Highest", "Critical"};
        }
        if (groups.contains("role") || groups.contains("security")) {
            return new String[]{"High", "Major"};
        }
        if (groups.contains("negative")) {
            return new String[]{"Medium", "Moderate"};
        }
        return new String[]{"Medium", "Minor"};
    }

    private static String topFrames(Throwable t, int max) {
        if (t == null) {
            return "n/a";
        }
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        String[] lines = sw.toString().split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(max, lines.length); i++) {
            sb.append(lines[i]).append("\n");
        }
        return sb.toString().trim();
    }

    private static void appendCsv(String id, String summary, String priority, String module, List<String> groups,
                                  List<String> steps, String actual, String screenshot, String browser) throws IOException {
        Path csv = ReportPaths.defects().resolve("jira-import.csv");
        StringBuilder desc = new StringBuilder("Steps to Reproduce:\n");
        for (int i = 0; i < steps.size(); i++) {
            desc.append(i + 1).append(". ").append(steps.get(i)).append("\n");
        }
        desc.append("\nActual Result: ").append(actual);
        if (screenshot != null) {
            desc.append("\nScreenshot: screenshots/").append(screenshot);
        }
        StringBuilder row = new StringBuilder();
        row.append(q(id)).append(',').append(q(summary)).append(',').append(q("Bug")).append(',')
           .append(q(priority)).append(',').append(q(module)).append(',')
           .append(q("automation " + String.join(" ", groups))).append(',')
           .append(q(browser)).append(',').append(q(desc.toString())).append("\n");
        synchronized (CSV_LOCK) {
            if (!Files.exists(csv)) {
                Files.write(csv, "Defect ID,Summary,Issue Type,Priority,Component,Labels,Environment,Description\n"
                        .getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            }
            Files.write(csv, row.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }
    }

    private static String q(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
