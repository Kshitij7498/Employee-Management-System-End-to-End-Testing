package com.ems.pages;

import com.ems.config.ConfigReader;
import com.ems.utils.StepLog;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds", 10)));
    }

    protected WebElement visible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void click(By by, String name) {
        StepLog.add("Click on " + name);
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    protected void type(By by, String value, String name) {
        WebElement element = visible(by);
        element.clear();
        if (value != null && !value.isEmpty()) {
            element.sendKeys(value);
        }
        boolean secret = name.toLowerCase().contains("password");
        String shown = secret ? "********" : value;
        StepLog.add("Enter '" + shown + "' in " + name);
    }

    protected void selectByText(By by, String text, String name) {
        new org.openqa.selenium.support.ui.Select(visible(by)).selectByVisibleText(text);
        StepLog.add("Select '" + text + "' in " + name);
    }

    protected void setValueWithJs(By by, String value, String name) {
        WebElement element = visible(by);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                        + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                element, value);
        StepLog.add("Set '" + value + "' in " + name);
    }

    /** Text of an element that must be visible (waits). */
    protected String text(By by) {
        return visible(by).getText().trim();
    }

    /** Waits up to 5s for non-empty text; returns "" if nothing appears (so assertions fail with a clear diff). */
    protected String waitForText(By by) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5)).until((ExpectedCondition<String>) d -> {
                String t = d.findElement(by).getText().trim();
                return t.isEmpty() ? null : t;
            });
        } catch (TimeoutException e) {
            return "";
        }
    }

    /** Text right now, without waiting. */
    protected String currentText(By by) {
        List<WebElement> found = driver.findElements(by);
        return found.isEmpty() ? "" : found.get(0).getText().trim();
    }

    /** True when the element exists and is displayed right now (no waiting). */
    protected boolean isDisplayedNow(By by) {
        List<WebElement> found = driver.findElements(by);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }

    /** True when the element becomes visible within the explicit wait. */
    protected boolean waitUntilDisplayed(By by) {
        try {
            visible(by);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean waitUntilGone(By by) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected String attribute(By by, String attribute) {
        String value = visible(by).getAttribute(attribute);
        return value == null ? "" : value;
    }
}
