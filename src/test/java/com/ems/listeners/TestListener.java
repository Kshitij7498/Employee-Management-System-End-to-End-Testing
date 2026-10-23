package com.ems.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.ems.driver.DriverFactory;
import com.ems.utils.DefectReportGenerator;
import com.ems.utils.ExtentManager;
import com.ems.utils.ScreenshotUtil;
import com.ems.utils.StepLog;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Hooked into every test via @Listeners on BaseTest.
 * - Builds the Extent execution report
 * - On failure: screenshot + Jira-style defect report
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        StepLog.reset();
        String description = result.getMethod().getDescription();
        String name = (description == null || description.isEmpty()) ? result.getMethod().getMethodName() : description;
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            name += " [" + params[0] + "]";
        }
        ExtentTest test = ExtentManager.startTest(name, result.getTestClass().getRealClass().getSimpleName());
        test.assignCategory(result.getMethod().getGroups());
        System.out.println("\n>>> START: " + name);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentManager.current();
        if (test != null) {
            test.pass("Test passed");
        }
        System.out.println("<<< PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        String screenshot = driver == null ? null : ScreenshotUtil.capture(driver, result.getMethod().getMethodName());
        String defectId = DefectReportGenerator.generate(result, screenshot, StepLog.get());

        ExtentTest test = ExtentManager.current();
        if (test != null) {
            test.fail(result.getThrowable());
            if (screenshot != null) {
                test.addScreenCaptureFromPath("screenshots/" + screenshot, "Failure screenshot");
            }
            test.info("Defect report generated: " + defectId + " (see defects folder of this run)");
        }
        System.out.println("<<< FAIL -> " + defectId + " | screenshot: " + screenshot);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentManager.current();
        if (test != null) {
            test.skip(result.getThrowable() == null ? "Test skipped" : String.valueOf(result.getThrowable()));
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }
}
