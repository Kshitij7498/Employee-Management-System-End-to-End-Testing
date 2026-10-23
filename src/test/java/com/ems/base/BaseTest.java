package com.ems.base;

import com.ems.driver.DriverFactory;
import com.ems.listeners.TestListener;
import com.ems.pages.DashboardPage;
import com.ems.pages.LoginPage;
import com.ems.utils.AppLocator;
import com.ems.utils.Constants;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Every test gets a brand-new browser session. Because each session starts with a fresh
 * browser profile, localStorage is empty, so the application re-seeds its default data
 * and tests are fully independent of each other.
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected WebDriver driver;

    /** Override to open the app with a query string (used by the defect demo suite). */
    protected String startUrl() {
        return AppLocator.url();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.createDriver();
        driver.get(startUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected DashboardPage loginAsAdmin() {
        return new LoginPage(driver).loginAs(Constants.ADMIN_USER, Constants.ADMIN_PASS);
    }

    protected DashboardPage loginAsHr() {
        return new LoginPage(driver).loginAs(Constants.HR_USER, Constants.HR_PASS);
    }

    protected DashboardPage loginAsEmployee() {
        return new LoginPage(driver).loginAs(Constants.EMP_USER, Constants.EMP_PASS);
    }
}
