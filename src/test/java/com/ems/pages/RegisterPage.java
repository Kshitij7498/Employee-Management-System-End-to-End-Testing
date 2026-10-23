package com.ems.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterPage extends BasePage {

    public static final String FULL_NAME = "regFullName";
    public static final String EMAIL = "regEmail";
    public static final String USERNAME = "regUsername";
    public static final String PASSWORD = "regPassword";
    public static final String CONFIRM = "regConfirmPassword";

    private final By view = By.id("registerView");
    private final By registerButton = By.id("registerBtn");
    private final By backToLogin = By.id("showLoginLink");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return waitUntilDisplayed(view);
    }

    public void waitUntilDisplayed() {
        visible(view);
    }

    public RegisterPage fill(String fullName, String email, String username, String password, String confirm) {
        type(By.id(FULL_NAME), fullName, "Full Name field");
        type(By.id(EMAIL), email, "Email field");
        type(By.id(USERNAME), username, "Username field");
        type(By.id(PASSWORD), password, "Password field");
        type(By.id(CONFIRM), confirm, "Confirm Password field");
        return this;
    }

    public RegisterPage submit() {
        click(registerButton, "Register button");
        return this;
    }

    public RegisterPage register(String fullName, String email, String username, String password, String confirm) {
        fill(fullName, email, username, password, confirm);
        return submit();
    }

    /** Validation message for a field id (waits for it to appear). */
    public String error(String fieldId) {
        return waitForText(By.id(fieldId + "-error"));
    }

    public LoginPage backToLogin() {
        click(backToLogin, "'Back to login' link");
        LoginPage login = new LoginPage(driver);
        login.isDisplayed();
        return login;
    }
}
