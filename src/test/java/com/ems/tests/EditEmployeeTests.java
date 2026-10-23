package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.pages.DashboardPage;
import com.ems.pages.EmployeeFormPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/** Uses seeded employee EMP001 = John Smith, Engineering, Software Engineer, 75000. */
public class EditEmployeeTests extends BaseTest {

    @Test(groups = {"regression", "functional"}, description = "TC_EDIT_001 - Edit form is pre-filled with employee data")
    public void editFormIsPrefilled() {
        EmployeeFormPage form = loginAsAdmin().clickEdit("EMP001");
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(form.title(), "Edit Employee", "Modal title");
        soft.assertEquals(form.value(EmployeeFormPage.ID_DISPLAY), "EMP001", "Employee ID");
        soft.assertEquals(form.value(EmployeeFormPage.FIRST_NAME), "John", "First name");
        soft.assertEquals(form.value(EmployeeFormPage.LAST_NAME), "Smith", "Last name");
        soft.assertEquals(form.value(EmployeeFormPage.EMAIL), "john.smith@company.com", "Email");
        soft.assertEquals(form.value(EmployeeFormPage.PHONE), "9876543210", "Phone");
        soft.assertEquals(form.selectedDepartment(), "Engineering", "Department");
        soft.assertEquals(form.value(EmployeeFormPage.DESIGNATION), "Software Engineer", "Designation");
        soft.assertEquals(form.value(EmployeeFormPage.SALARY), "75000", "Salary");
        soft.assertEquals(form.value(EmployeeFormPage.JOINING_DATE), "2021-03-15", "Joining date");
        soft.assertAll();
    }

    @Test(groups = {"smoke", "regression", "functional"}, description = "TC_EDIT_002 - Update designation and salary")
    public void updateDesignationAndSalary() {
        DashboardPage dashboard = loginAsAdmin();
        EmployeeFormPage form = dashboard.clickEdit("EMP001");
        form.setDesignation("Senior Software Engineer").setSalary("90000").save();

        Assert.assertEquals(dashboard.toast(), "Employee updated successfully", "Success toast");
        Assert.assertEquals(dashboard.cellText("EMP001", "c-designation"), "Senior Software Engineer", "Designation");
        Assert.assertEquals(dashboard.cellText("EMP001", "c-salary"), "90,000", "Salary");
        Assert.assertEquals(dashboard.rowCount(), 5, "Editing must not create a new row");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_EDIT_003 - Edited data persists after refresh")
    public void editedDataPersistsAfterRefresh() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickEdit("EMP002").setDesignation("Head of HR").save();
        dashboard.toast();
        driver.navigate().refresh();
        dashboard.waitUntilLoaded();
        Assert.assertEquals(dashboard.cellText("EMP002", "c-designation"), "Head of HR", "Designation after refresh");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_EDIT_004 - Editing to another employee's email is rejected")
    public void duplicateEmailOnEditRejected() {
        EmployeeFormPage form = loginAsAdmin().clickEdit("EMP001");
        form.setEmail("priya.sharma@company.com").save();
        Assert.assertEquals(form.error(EmployeeFormPage.EMAIL), "Email already exists", "Duplicate email error");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_EDIT_005 - Saving without changing the email is allowed")
    public void keepingOwnEmailIsAllowed() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickEdit("EMP001").save();
        Assert.assertEquals(dashboard.toast(), "Employee updated successfully", "Own email must not be flagged as duplicate");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_EDIT_006 - Clearing a required field blocks the update")
    public void clearingRequiredFieldBlocksUpdate() {
        DashboardPage dashboard = loginAsAdmin();
        EmployeeFormPage form = dashboard.clickEdit("EMP001");
        form.setFirstName("").save();
        Assert.assertEquals(form.error(EmployeeFormPage.FIRST_NAME), "First name is required", "Required error");
        form.cancel();
        Assert.assertEquals(dashboard.cellText("EMP001", "c-name"), "John Smith", "Record must be unchanged");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_EDIT_007 - Cancel edit leaves the record unchanged")
    public void cancelEditLeavesRecordUnchanged() {
        DashboardPage dashboard = loginAsAdmin();
        EmployeeFormPage form = dashboard.clickEdit("EMP001");
        form.setDesignation("Should Not Be Saved").setSalary("1");
        form.cancel();
        Assert.assertEquals(dashboard.cellText("EMP001", "c-designation"), "Software Engineer", "Designation");
        Assert.assertEquals(dashboard.cellText("EMP001", "c-salary"), "75,000", "Salary");
    }

    @Test(groups = {"regression", "functional", "role"}, description = "TC_EDIT_008 - HR can edit an employee")
    public void hrCanEditEmployee() {
        DashboardPage dashboard = loginAsHr();
        dashboard.clickEdit("EMP003").setDesignation("Senior Accountant").save();
        Assert.assertEquals(dashboard.toast(), "Employee updated successfully", "Success toast");
        Assert.assertEquals(dashboard.cellText("EMP003", "c-designation"), "Senior Accountant", "Designation");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_EDIT_009 - Employee ID cannot be edited")
    public void employeeIdIsReadOnly() {
        EmployeeFormPage form = loginAsAdmin().clickEdit("EMP001");
        Assert.assertTrue(form.isReadOnly(EmployeeFormPage.ID_DISPLAY), "Employee ID field must be read-only");
    }
}
