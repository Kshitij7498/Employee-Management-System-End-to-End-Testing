package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.model.Employee;
import com.ems.pages.DashboardPage;
import com.ems.pages.EmployeeFormPage;
import com.ems.utils.Constants;
import com.ems.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddEmployeeTests extends BaseTest {

    @Test(groups = {"smoke", "regression", "functional"}, description = "TC_ADD_001 - Admin adds a new employee")
    public void adminAddsEmployee() {
        DashboardPage dashboard = loginAsAdmin();
        Employee e = TestDataFactory.validEmployee();
        EmployeeFormPage form = dashboard.clickAdd();
        Assert.assertEquals(form.title(), "Add Employee", "Modal title");
        form.submit(e);

        Assert.assertEquals(dashboard.toast(), "Employee added successfully", "Success toast");
        Assert.assertTrue(form.waitUntilClosed(), "Form should close after save");
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT + 1, "Row count");
        Assert.assertTrue(dashboard.hasEmployee("EMP006"), "Auto-generated ID EMP006 should be listed");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-name"), e.fullName(), "Name");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-email"), e.email(), "Email");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-dept"), "Engineering", "Department");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-salary"), "65,000", "Salary");
        Assert.assertEquals(dashboard.countText(), "Showing 6 of 6 employees", "Counter label");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_ADD_002 - HR adds a new employee")
    public void hrAddsEmployee() {
        DashboardPage dashboard = loginAsHr();
        dashboard.clickAdd().submit(TestDataFactory.validEmployee().department("Sales"));
        Assert.assertEquals(dashboard.toast(), "Employee added successfully", "Success toast");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-dept"), "Sales", "Department");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_ADD_003 - Added employee persists after refresh")
    public void addedEmployeePersistsAfterRefresh() {
        DashboardPage dashboard = loginAsAdmin();
        Employee e = TestDataFactory.validEmployee();
        dashboard.clickAdd().submit(e);
        dashboard.toast();
        driver.navigate().refresh();
        dashboard.waitUntilLoaded();
        Assert.assertTrue(dashboard.hasEmployee("EMP006"), "Employee should still exist after refresh");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-email"), e.email(), "Email after refresh");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_ADD_004 - Add form is empty when reopened")
    public void addFormIsEmptyWhenReopened() {
        DashboardPage dashboard = loginAsAdmin();
        EmployeeFormPage form = dashboard.clickAdd();
        form.setFirstName("Leftover").setLastName("Data");
        form.cancel();
        form = dashboard.clickAdd();
        Assert.assertEquals(form.value(EmployeeFormPage.FIRST_NAME), "", "First name should be cleared");
        Assert.assertEquals(form.value(EmployeeFormPage.LAST_NAME), "", "Last name should be cleared");
        Assert.assertEquals(form.value(EmployeeFormPage.ID_DISPLAY), "Auto-generated", "ID placeholder");
    }
}
