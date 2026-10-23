package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.pages.DashboardPage;
import com.ems.pages.LoginPage;
import com.ems.pages.RegisterPage;
import com.ems.utils.Constants;
import com.ems.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/** Role-based access control. ADMIN = full, HR = add/edit (no delete, no users), EMPLOYEE = read-only. */
public class RoleAccessTests extends BaseTest {

    @Test(groups = {"smoke", "regression", "role"}, description = "TC_ROLE_001 - Admin has full privileges")
    public void adminHasFullPrivileges() {
        DashboardPage d = loginAsAdmin();
        SoftAssert soft = new SoftAssert();
        soft.assertTrue(d.isAddVisible(), "Admin should see Add Employee");
        soft.assertTrue(d.isEditVisible("EMP001"), "Admin should see Edit");
        soft.assertTrue(d.isDeleteVisible("EMP001"), "Admin should see Delete");
        soft.assertTrue(d.isUsersNavVisible(), "Admin should see Users menu");
        soft.assertTrue(d.isSalaryColumnVisible(), "Admin should see Salary column");
        soft.assertAll();
    }

    @Test(groups = {"smoke", "regression", "role"}, description = "TC_ROLE_002 - HR can add/edit but not delete or manage users")
    public void hrHasLimitedPrivileges() {
        DashboardPage d = loginAsHr();
        SoftAssert soft = new SoftAssert();
        soft.assertTrue(d.isAddVisible(), "HR should see Add Employee");
        soft.assertTrue(d.isEditVisible("EMP001"), "HR should see Edit");
        soft.assertFalse(d.isDeleteVisible("EMP001"), "HR must NOT see Delete");
        soft.assertFalse(d.isUsersNavVisible(), "HR must NOT see Users menu");
        soft.assertTrue(d.isSalaryColumnVisible(), "HR should see Salary column");
        soft.assertAll();
    }

    @Test(groups = {"smoke", "regression", "role"}, description = "TC_ROLE_003 - Employee role is read-only")
    public void employeeIsReadOnly() {
        DashboardPage d = loginAsEmployee();
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(d.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "Employee can view the directory");
        soft.assertFalse(d.isAddVisible(), "Employee must NOT see Add Employee");
        soft.assertFalse(d.isEditVisible("EMP001"), "Employee must NOT see Edit");
        soft.assertFalse(d.isDeleteVisible("EMP001"), "Employee must NOT see Delete");
        soft.assertFalse(d.isUsersNavVisible(), "Employee must NOT see Users menu");
        soft.assertAll();
    }

    @Test(groups = {"regression", "role", "security"}, description = "TC_ROLE_004 - Salary is hidden from the Employee role")
    public void salaryHiddenFromEmployeeRole() {
        DashboardPage d = loginAsEmployee();
        Assert.assertFalse(d.isSalaryColumnVisible(), "Salary column must be hidden");
        Assert.assertFalse(d.hasEmployeeSalaryCell("EMP001"), "Salary cells must not be rendered at all");
    }

    @Test(groups = {"regression", "role"}, description = "TC_ROLE_005 - Admin can view all user accounts")
    public void adminSeesUserAccounts() {
        DashboardPage d = loginAsAdmin().openUsers();
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(d.usersRowCount(), 3, "Seeded user count");
        soft.assertEquals(d.userRole("admin"), "ADMIN", "admin role");
        soft.assertEquals(d.userRole("hr"), "HR", "hr role");
        soft.assertEquals(d.userRole("employee"), "EMPLOYEE", "employee role");
        soft.assertAll();
    }

    @Test(groups = {"regression", "role", "security"}, description = "TC_ROLE_006 - Privileges do not leak between sessions")
    public void privilegesDoNotLeakBetweenUsers() {
        DashboardPage d = loginAsAdmin();
        Assert.assertTrue(d.isDeleteVisible("EMP001"), "Precondition: admin sees Delete");
        LoginPage login = d.logout();
        d = login.loginAs(Constants.EMP_USER, Constants.EMP_PASS);
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(d.roleBadge(), "EMPLOYEE", "Role badge");
        soft.assertFalse(d.isAddVisible(), "Add must be hidden after switching to Employee");
        soft.assertFalse(d.isDeleteVisible("EMP001"), "Delete must be hidden after switching to Employee");
        soft.assertFalse(d.isUsersNavVisible(), "Users menu must be hidden after switching to Employee");
        soft.assertAll();
    }

    @Test(groups = {"regression", "role"}, description = "TC_ROLE_007 - Newly registered user is read-only")
    public void registeredUserIsReadOnly() {
        String user = TestDataFactory.uniqueUsername();
        new LoginPage(driver).openRegister()
                .register("Nina Newhire", user + "@company.com", user, "Qa@Pass123", "Qa@Pass123");
        DashboardPage d = new LoginPage(driver).loginAs(user, "Qa@Pass123");
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(d.roleBadge(), "EMPLOYEE", "Role badge");
        soft.assertFalse(d.isAddVisible(), "Add must be hidden");
        soft.assertFalse(d.isEditVisible("EMP001"), "Edit must be hidden");
        soft.assertFalse(d.isDeleteVisible("EMP001"), "Delete must be hidden");
        soft.assertAll();
    }

    @Test(groups = {"regression", "role"}, description = "TC_ROLE_008 - Admin sees a newly registered user")
    public void adminSeesNewlyRegisteredUser() {
        String user = TestDataFactory.uniqueUsername();
        RegisterPage reg = new LoginPage(driver).openRegister();
        reg.register("Nina Newhire", user + "@company.com", user, "Qa@Pass123", "Qa@Pass123");
        DashboardPage d = new LoginPage(driver).loginAs(Constants.ADMIN_USER, Constants.ADMIN_PASS).openUsers();
        Assert.assertTrue(d.hasUser(user), "Registered user should be listed");
        Assert.assertEquals(d.userRole(user), "EMPLOYEE", "Registered user's role");
        Assert.assertEquals(d.usersRowCount(), 4, "User count");
    }
}
