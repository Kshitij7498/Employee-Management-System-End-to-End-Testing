package com.ems.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Records every UI action performed by a test. The steps are shown in the execution
 * report and reused as "Steps to Reproduce" in the Jira-style defect report.
 */
public final class StepLog {

    private static final ThreadLocal<List<String>> STEPS = ThreadLocal.withInitial(ArrayList::new);

    private StepLog() {
    }

    public static void reset() {
        STEPS.get().clear();
    }

    public static void add(String step) {
        STEPS.get().add(step);
        System.out.println("  [STEP] " + step);
        ExtentManager.info(step);
    }

    public static List<String> get() {
        return new ArrayList<>(STEPS.get());
    }
}
