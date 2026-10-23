package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.pages.DashboardPage;
import com.ems.pages.LoginPage;
import com.ems.pages.RegisterPage;
import com.ems.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class RegistrationTests extends BaseTest {

    private static final String VALID_PASSWORD = "Qa@Pass123";

    private RegisterPage openRegister() {
        return new LoginPage(driver).openRegister();
    }

    @Test(groups = {"smoke", "regression", "functional"}, description = "TC_REG_001 - Register a new user and login")
    public void registerNewUserAndLogin() {
        String user = TestDataFactory.uniqueUsername();
        openRegister().register("Quincy Analyst", user + "@company.com", user, VALID_PASSWORD, VALID_PASSWORD);

        LoginPage login = new LoginPage(driver);
        Assert.assertEquals(login.successMessage(), "Registration successful. Please login.", "Success message");

        DashboardPage dashboard = login.loginAs(user, VALID_PASSWORD);
        Assert.assertEquals(dashboard.roleBadge(), "EMPLOYEE", "New users get the EMPLOYEE role");
        Assert.assertTrue(dashboard.welcomeText().contains("Quincy Analyst"), "Welcome text shows the full name");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_REG_002 - Empty form shows required-field errors")
    public void emptyFormShowsRequiredErrors() {
        RegisterPage reg = openRegister();
        reg.submit();
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(reg.error(RegisterPage.FULL_NAME), "Full name is required", "Full name");
        soft.assertEquals(reg.error(RegisterPage.EMAIL), "Email is required", "Email");
        soft.assertEquals(reg.error(RegisterPage.USERNAME), "Username is required", "Username");
        soft.assertEquals(reg.error(RegisterPage.PASSWORD), "Password is required", "Password");
        soft.assertEquals(reg.error(RegisterPage.CONFIRM), "Confirm password is required", "Confirm password");
        soft.assertAll();
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][]{{"plainaddress"}, {"user@"}, {"user@domain"}, {"@domain.com"}, {"user name@x.com"}};
    }

    @Test(dataProvider = "invalidEmails", groups = {"regression", "negative"},
            description = "TC_REG_003 - Invalid email formats are rejected")
    public void invalidEmailRejected(String email) {
        RegisterPage reg = openRegister();
        String user = TestDataFactory.uniqueUsername();
        reg.register("Quincy Analyst", email, user, VALID_PASSWORD, VALID_PASSWORD);
        Assert.assertEquals(reg.error(RegisterPage.EMAIL), "Enter a valid email address", "Email error for " + email);
    }

    @DataProvider(name = "weakPasswords")
    public Object[][] weakPasswords() {
        return new Object[][]{{"short1!"}, {"alllowercase1!"}, {"ALLUPPERCASE1!"}, {"NoNumbers!!"}, {"NoSpecial123"}};
    }

    @Test(dataProvider = "weakPasswords", groups = {"regression", "negative", "security"},
            description = "TC_REG_004 - Weak passwords are rejected")
    public void weakPasswordRejected(String password) {
        RegisterPage reg = openRegister();
        String user = TestDataFactory.uniqueUsername();
        reg.register("Quincy Analyst", user + "@company.com", user, password, password);
        Assert.assertEquals(reg.error(RegisterPage.PASSWORD),
                "Password must be at least 8 characters with uppercase, lowercase, number and special character",
                "Password error for " + password);
    }

    @Test(groups = {"regression", "negative"}, description = "TC_REG_005 - Password and confirm password must match")
    public void passwordMismatchRejected() {
        RegisterPage reg = openRegister();
        String user = TestDataFactory.uniqueUsername();
        reg.register("Quincy Analyst", user + "@company.com", user, VALID_PASSWORD, "Different@123");
        Assert.assertEquals(reg.error(RegisterPage.CONFIRM), "Passwords do not match", "Confirm password error");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_REG_006 - Duplicate username is rejected")
    public void duplicateUsernameRejected() {
        RegisterPage reg = openRegister();
        reg.register("Another Admin", "another.admin@company.com", "admin", VALID_PASSWORD, VALID_PASSWORD);
        Assert.assertEquals(reg.error(RegisterPage.USERNAME), "Username already exists", "Username error");
    }

    @DataProvider(name = "invalidUsernames")
    public Object[][] invalidUsernames() {
        return new Object[][]{{"ab"}, {"user name"}, {"user@123"}, {"averyveryverylongusername"}};
    }

    @Test(dataProvider = "invalidUsernames", groups = {"regression", "negative"},
            description = "TC_REG_007 - Invalid username formats are rejected")
    public void invalidUsernameRejected(String username) {
        RegisterPage reg = openRegister();
        reg.register("Quincy Analyst", "quincy.invalid@company.com", username, VALID_PASSWORD, VALID_PASSWORD);
        Assert.assertEquals(reg.error(RegisterPage.USERNAME), "Username must be 4-15 alphanumeric characters",
                "Username error for '" + username + "'");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_REG_008 - Full name with digits is rejected")
    public void fullNameWithDigitsRejected() {
        RegisterPage reg = openRegister();
        String user = TestDataFactory.uniqueUsername();
        reg.register("John123", user + "@company.com", user, VALID_PASSWORD, VALID_PASSWORD);
        Assert.assertEquals(reg.error(RegisterPage.FULL_NAME), "Full name must contain only letters", "Full name error");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_REG_009 - Navigate between login and registration")
    public void navigateBetweenLoginAndRegistration() {
        RegisterPage reg = openRegister();
        Assert.assertTrue(reg.isDisplayed(), "Registration page should be displayed");
        LoginPage login = reg.backToLogin();
        Assert.assertTrue(login.isDisplayed(), "Login page should be displayed again");
    }
}
