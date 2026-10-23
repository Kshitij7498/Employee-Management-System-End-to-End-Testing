package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.pages.DashboardPage;
import com.ems.utils.Constants;
import com.ems.utils.TestDataFactory;
import com.ems.model.Employee;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

/**
 * Seeded data: EMP001 John Smith (Engineering) | EMP002 Priya Sharma (HR) | EMP003 Rahul Verma (Finance)
 *              EMP004 Emily Johnson (Marketing) | EMP005 Michael Brown (Sales)
 */
public class SearchTests extends BaseTest {

    @DataProvider(name = "uniqueMatches")
    public Object[][] uniqueMatches() {
        return new Object[][]{
                {"Priya", "EMP002"},                       // first name
                {"verma", "EMP003"},                       // last name
                {"priya.sharma@company.com", "EMP002"},    // email
                {"Marketing", "EMP004"},                   // department / designation
                {"EMP005", "EMP005"},                      // employee ID
                {"engin", "EMP001"}                        // partial match
        };
    }

    @Test(dataProvider = "uniqueMatches", groups = {"smoke", "regression", "functional"},
            description = "TC_SRCH_001 - Search finds an employee by name, email, department, ID or partial text")
    public void searchFindsEmployee(String term, String expectedId) {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search(term);
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList(expectedId), "Rows for '" + term + "'");
    }

    @DataProvider(name = "caseVariants")
    public Object[][] caseVariants() {
        return new Object[][]{{"SMITH"}, {"sMiTh"}, {"JOHN SMITH"}};
    }

    @Test(dataProvider = "caseVariants", groups = {"regression", "functional"},
            description = "TC_SRCH_002 - Search is case-insensitive")
    public void searchIsCaseInsensitive(String term) {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search(term);
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP001"), "Rows for '" + term + "'");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_SRCH_003 - No match shows 'No employees found'")
    public void noMatchShowsMessage() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search("zzzzzz");
        Assert.assertEquals(dashboard.rowCount(), 0, "Row count");
        Assert.assertTrue(dashboard.isNoResultsDisplayed(), "No-results message should be visible");
        Assert.assertEquals(dashboard.noResultsText(), "No employees found", "Message text");
        Assert.assertEquals(dashboard.countText(), "Showing 0 of 5 employees", "Counter label");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_SRCH_004 - Clear button restores the full list")
    public void clearRestoresFullList() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search("zzzzzz");
        dashboard.clearSearch();
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "All employees should be shown again");
        Assert.assertFalse(dashboard.isNoResultsDisplayed(), "No-results message should disappear");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_SRCH_005 - Department filter narrows the list")
    public void departmentFilterWorks() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.filterDepartment("HR");
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP002"), "HR employees");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_SRCH_006 - Search and department filter work together")
    public void searchAndFilterCombined() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search("company.com");
        Assert.assertEquals(dashboard.rowCount(), 5, "All seeded employees share the company.com domain");
        dashboard.filterDepartment("Sales");
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP005"), "Combined result");
        Assert.assertEquals(dashboard.countText(), "Showing 1 of 5 employees", "Counter label");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_SRCH_007 - Leading/trailing spaces are ignored")
    public void searchTrimsWhitespace() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search("   priya   ");
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP002"), "Rows");
    }

    @DataProvider(name = "specialInputs")
    public Object[][] specialInputs() {
        return new Object[][]{{"%$#@!"}, {"<script>alert(1)</script>"}, {"' OR '1'='1"}};
    }

    @Test(dataProvider = "specialInputs", groups = {"regression", "negative", "security"},
            description = "TC_SRCH_008 - Special characters are handled safely")
    public void specialCharactersHandledSafely(String term) {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search(term);
        Assert.assertEquals(dashboard.rowCount(), 0, "No rows expected for '" + term + "'");
        Assert.assertTrue(dashboard.isNoResultsDisplayed(), "Application should show the no-results message");
        dashboard.clearSearch();
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "Application must still work afterwards");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_SRCH_009 - Partial term returns multiple matches and updates the counter")
    public void partialTermReturnsMultipleMatches() {
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search("john");   // matches John Smith and Emily Johnson
        Assert.assertEquals(dashboard.visibleEmployeeIds().toString(), "[EMP001, EMP004]", "Rows");
        Assert.assertEquals(dashboard.countText(), "Showing 2 of 5 employees", "Counter label");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_SRCH_010 - Newly added employee is searchable")
    public void newlyAddedEmployeeIsSearchable() {
        DashboardPage dashboard = loginAsAdmin();
        Employee e = TestDataFactory.validEmployee();
        dashboard.clickAdd().submit(e);
        dashboard.toast();
        dashboard.search(e.email());
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP006"), "Rows");
    }

    @Test(groups = {"regression", "functional", "role"}, description = "TC_SRCH_011 - Employee role can search")
    public void employeeRoleCanSearch() {
        DashboardPage dashboard = loginAsEmployee();
        dashboard.search("priya");
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP002"), "Rows");
    }
}
