package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.pages.DashboardPage;
import com.ems.pages.LoginPage;
import com.ems.utils.Constants;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    private static final String INVALID = "Invalid username or password";
    private static final String LOCKED = "Account locked after 3 failed login attempts";

    @DataProvider(name = "validUsers")
    public Object[][] validUsers() {
        return new Object[][]{
                {Constants.ADMIN_USER, Constants.ADMIN_PASS, "ADMIN"},
                {Constants.HR_USER, Constants.HR_PASS, "HR"},
                {Constants.EMP_USER, Constants.EMP_PASS, "EMPLOYEE"}
        };
    }

    @Test(dataProvider = "validUsers", groups = {"smoke", "regression", "functional"},
            description = "TC_LOGIN_001 - Valid login for each role")
    public void validLoginForEachRole(String user, String pass, String expectedRole) {
        DashboardPage dashboard = new LoginPage(driver).loginAs(user, pass);
        Assert.assertTrue(dashboard.isLoaded(), "Dashboard should be displayed after a valid login");
        Assert.assertEquals(dashboard.roleBadge(), expectedRole, "Role badge");
        Assert.assertTrue(dashboard.welcomeText().startsWith("Welcome, "), "Welcome message");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_LOGIN_002 - Wrong password shows error")
    public void wrongPasswordShowsError() {
        LoginPage login = new LoginPage(driver);
        login.login(Constants.ADMIN_USER, "WrongPass@1");
        Assert.assertEquals(login.errorMessage(), INVALID, "Error message");
        Assert.assertTrue(login.isDisplayed(), "User must stay on the login page");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_LOGIN_003 - Unknown username shows error")
    public void unknownUserShowsError() {
        LoginPage login = new LoginPage(driver);
        login.login("nosuchuser", "Whatever@123");
        Assert.assertEquals(login.errorMessage(), INVALID, "Error message");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_LOGIN_004 - Empty credentials are rejected")
    public void emptyCredentialsRejected() {
        LoginPage login = new LoginPage(driver);
        login.clickLogin();
        Assert.assertEquals(login.errorMessage(), "Username and password are required", "Error message");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_LOGIN_005 - Empty password is rejected")
    public void emptyPasswordRejected() {
        LoginPage login = new LoginPage(driver);
        login.enterUsername(Constants.ADMIN_USER).clickLogin();
        Assert.assertEquals(login.errorMessage(), "Username and password are required", "Error message");
    }

    @Test(groups = {"regression", "negative", "security"}, description = "TC_LOGIN_006 - Password is case sensitive")
    public void passwordIsCaseSensitive() {
        LoginPage login = new LoginPage(driver);
        login.login(Constants.ADMIN_USER, Constants.ADMIN_PASS.toLowerCase());
        Assert.assertEquals(login.errorMessage(), INVALID, "Error message");
    }

    @Test(groups = {"regression", "negative", "security"}, description = "TC_LOGIN_007 - Account locks after 3 failed attempts")
    public void accountLocksAfterThreeFailedAttempts() {
        LoginPage login = new LoginPage(driver);
        login.login(Constants.ADMIN_USER, "Bad@Pass1");
        Assert.assertEquals(login.errorMessage(), INVALID, "Attempt 1");
        login.login(Constants.ADMIN_USER, "Bad@Pass2");
        Assert.assertEquals(login.errorMessage(), INVALID, "Attempt 2");
        login.login(Constants.ADMIN_USER, "Bad@Pass3");
        Assert.assertEquals(login.errorMessage(), LOCKED, "Attempt 3 must lock the account");
    }

    @Test(groups = {"regression", "negative", "security"}, description = "TC_LOGIN_008 - Locked account rejects the correct password")
    public void lockedAccountRejectsCorrectPassword() {
        LoginPage login = new LoginPage(driver);
        for (int i = 1; i <= 3; i++) {
            login.login(Constants.ADMIN_USER, "Bad@Pass" + i);
        }
        Assert.assertEquals(login.errorMessage(), LOCKED, "Account should be locked");
        login.login(Constants.ADMIN_USER, Constants.ADMIN_PASS);
        Assert.assertEquals(login.errorMessage(), LOCKED, "Correct password must still be rejected while locked");
        Assert.assertTrue(new DashboardPage(driver).isHidden(), "Dashboard must not be shown");
    }

    @Test(groups = {"regression", "security"}, description = "TC_LOGIN_009 - Password field is masked")
    public void passwordFieldIsMasked() {
        Assert.assertEquals(new LoginPage(driver).passwordFieldType(), "password", "Input type of password field");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_LOGIN_010 - Logout returns to the login page")
    public void logoutReturnsToLoginPage() {
        DashboardPage dashboard = loginAsAdmin();
        LoginPage login = dashboard.logout();
        Assert.assertTrue(login.isDisplayed(), "Login page should be displayed after logout");
        Assert.assertTrue(dashboard.isHidden(), "Employee data must not be visible after logout");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_LOGIN_011 - Session persists after page refresh")
    public void sessionPersistsAfterRefresh() {
        DashboardPage dashboard = loginAsAdmin();
        driver.navigate().refresh();
        Assert.assertTrue(dashboard.isLoaded(), "User should remain logged in after refresh");
        Assert.assertEquals(dashboard.roleBadge(), "ADMIN", "Role badge after refresh");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_LOGIN_012 - Pressing Enter submits the login form")
    public void enterKeySubmitsLoginForm() {
        LoginPage login = new LoginPage(driver);
        login.enterUsername(Constants.HR_USER).enterPassword(Constants.HR_PASS).pressEnterInPasswordField();
        Assert.assertTrue(new DashboardPage(driver).isLoaded(), "Dashboard should load after pressing Enter");
    }
}
