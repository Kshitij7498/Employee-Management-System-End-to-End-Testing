package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.pages.DashboardPage;
import com.ems.utils.Constants;
import com.ems.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteEmployeeTests extends BaseTest {

    @Test(groups = {"regression", "functional"}, description = "TC_DEL_001 - Delete confirmation names the employee")
    public void confirmationDialogNamesEmployee() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickDelete("EMP003");
        Assert.assertTrue(dashboard.isConfirmDisplayed(), "Confirmation dialog should be displayed");
        Assert.assertEquals(dashboard.confirmMessage(), "Are you sure you want to delete employee Rahul Verma?", "Dialog text");
        Assert.assertTrue(dashboard.hasEmployee("EMP003"), "Employee must not be removed before confirmation");
    }

    @Test(groups = {"smoke", "regression", "functional"}, description = "TC_DEL_002 - Confirming deletion removes the employee")
    public void confirmDeletionRemovesEmployee() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickDelete("EMP005").confirmDelete();
        Assert.assertEquals(dashboard.toast(), "Employee deleted successfully", "Success toast");
        Assert.assertFalse(dashboard.hasEmployee("EMP005"), "EMP005 should be removed");
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT - 1, "Row count");
        Assert.assertEquals(dashboard.countText(), "Showing 4 of 4 employees", "Counter label");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_DEL_003 - Cancelling deletion keeps the employee")
    public void cancelDeletionKeepsEmployee() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickDelete("EMP002").cancelDelete();
        Assert.assertFalse(dashboard.isConfirmDisplayed(), "Dialog should close");
        Assert.assertTrue(dashboard.hasEmployee("EMP002"), "Employee should still exist");
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "Row count unchanged");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_DEL_004 - Deleted employee cannot be found by search")
    public void deletedEmployeeNotFoundBySearch() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickDelete("EMP004").confirmDelete();
        dashboard.search("Emily");
        Assert.assertEquals(dashboard.rowCount(), 0, "Search should return nothing for a deleted employee");
        Assert.assertTrue(dashboard.isNoResultsDisplayed(), "'No employees found' message expected");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_DEL_005 - Deletion persists after refresh")
    public void deletionPersistsAfterRefresh() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickDelete("EMP001").confirmDelete();
        dashboard.toast();
        driver.navigate().refresh();
        dashboard.waitUntilLoaded();
        Assert.assertFalse(dashboard.hasEmployee("EMP001"), "EMP001 must stay deleted after refresh");
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT - 1, "Row count after refresh");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_DEL_006 - Delete multiple employees one after another")
    public void deleteMultipleEmployees() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickDelete("EMP001").confirmDelete();
        dashboard.clickDelete("EMP002").confirmDelete();
        dashboard.clickDelete("EMP003").confirmDelete();
        Assert.assertEquals(dashboard.rowCount(), 2, "Two employees should remain");
        Assert.assertEquals(dashboard.visibleEmployeeIds().toString(), "[EMP004, EMP005]", "Remaining IDs");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_DEL_007 - Newly added employee can be deleted")
    public void deleteNewlyAddedEmployee() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.clickAdd().submit(TestDataFactory.validEmployee());
        dashboard.toast();
        dashboard.clickDelete("EMP006").confirmDelete();
        Assert.assertFalse(dashboard.hasEmployee("EMP006"), "EMP006 should be deleted");
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "Back to the seeded count");
    }
}
