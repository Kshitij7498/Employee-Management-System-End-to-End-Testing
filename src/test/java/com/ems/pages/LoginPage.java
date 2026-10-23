package com.ems.pages;

import com.ems.utils.StepLog;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By view = By.id("loginView");
    private final By username = By.id("loginUsername");
    private final By password = By.id("loginPassword");
    private final By loginButton = By.id("loginBtn");
    private final By error = By.id("loginError");
    private final By success = By.id("loginSuccess");
    private final By registerLink = By.id("showRegisterLink");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return waitUntilDisplayed(view);
    }

    public LoginPage enterUsername(String value) {
        type(username, value, "Username field");
        return this;
    }

    public LoginPage enterPassword(String value) {
        type(password, value, "Password field");
        return this;
    }

    public LoginPage clickLogin() {
        click(loginButton, "Login button");
        return this;
    }

    public LoginPage login(String user, String pass) {
        enterUsername(user);
        enterPassword(pass);
        return clickLogin();
    }

    public DashboardPage loginAs(String user, String pass) {
        login(user, pass);
        DashboardPage dashboard = new DashboardPage(driver);
        dashboard.waitUntilLoaded();
        return dashboard;
    }

    public LoginPage pressEnterInPasswordField() {
        visible(password).sendKeys(Keys.ENTER);
        StepLog.add("Press ENTER in Password field");
        return this;
    }

    public String errorMessage() {
        return waitForText(error);
    }

    public String successMessage() {
        return waitForText(success);
    }

    public String passwordFieldType() {
        return attribute(password, "type");
    }

    public RegisterPage openRegister() {
        click(registerLink, "'Register here' link");
        RegisterPage page = new RegisterPage(driver);
        page.waitUntilDisplayed();
        return page;
    }
}
