package com.ems.utils;

import com.ems.config.ConfigReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/** One timestamped folder per execution: reports/run_yyyyMMdd_HHmmss/{ExecutionReport.html, screenshots, defects}. */
public final class ReportPaths {

    private static final Path ROOT = AppLocator.projectRoot().resolve(ConfigReader.get("reports.dir", "reports"));
    private static final Path RUN_DIR = ROOT.resolve("run_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));

    static {
        try {
            Files.createDirectories(RUN_DIR.resolve("screenshots"));
            Files.createDirectories(RUN_DIR.resolve("defects"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create report directories", e);
        }
    }

    private ReportPaths() {
    }

    public static Path root() { return ROOT; }
    public static Path runDir() { return RUN_DIR; }
    public static Path screenshots() { return RUN_DIR.resolve("screenshots"); }
    public static Path defects() { return RUN_DIR.resolve("defects"); }
    public static Path executionReport() { return RUN_DIR.resolve("ExecutionReport.html"); }
}
