package com.ems.driver;

import com.ems.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates browser sessions. Selenium Manager (built into Selenium 4.6+) downloads the
 * matching driver automatically, so no manual chromedriver setup is needed.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless", false);
        WebDriver driver;

        switch (browser) {
            case "firefox":
                FirefoxOptions firefox = new FirefoxOptions();
                if (headless) {
                    firefox.addArguments("-headless");
                }
                firefox.addArguments("--width=1366", "--height=900");
                driver = new FirefoxDriver(firefox);
                break;
            case "edge":
                EdgeOptions edge = new EdgeOptions();
                edge.addArguments("--window-size=1366,900", "--remote-allow-origins=*");
                if (headless) {
                    edge.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edge);
                break;
            case "chrome":
            default:
                ChromeOptions chrome = new ChromeOptions();
                chrome.addArguments("--window-size=1366,900", "--remote-allow-origins=*",
                        "--disable-notifications", "--no-first-run", "--no-default-browser-check",
                        "--disable-search-engine-choice-screen");
                if (headless) {
                    chrome.addArguments("--headless=new");
                }
                driver = new ChromeDriver(chrome);
                break;
        }

        if (!headless) {
            driver.manage().window().maximize();
        }
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
