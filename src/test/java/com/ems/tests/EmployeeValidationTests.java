package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.model.Employee;
import com.ems.pages.DashboardPage;
import com.ems.pages.EmployeeFormPage;
import com.ems.utils.Constants;
import com.ems.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;

/** Form validation (negative + boundary) for the Add Employee form. */
public class EmployeeValidationTests extends BaseTest {

    @Test(groups = {"regression", "negative"}, description = "TC_VAL_001 - Empty employee form shows all required errors")
    public void emptyFormShowsAllRequiredErrors() {
        DashboardPage dashboard = loginAsAdmin();
        EmployeeFormPage form = dashboard.clickAdd();
        form.save();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(form.error(EmployeeFormPage.FIRST_NAME), "First name is required", "First name");
        soft.assertEquals(form.error(EmployeeFormPage.LAST_NAME), "Last name is required", "Last name");
        soft.assertEquals(form.error(EmployeeFormPage.EMAIL), "Email is required", "Email");
        soft.assertEquals(form.error(EmployeeFormPage.PHONE), "Phone number is required", "Phone");
        soft.assertEquals(form.error(EmployeeFormPage.DEPARTMENT), "Department is required", "Department");
        soft.assertEquals(form.error(EmployeeFormPage.DESIGNATION), "Designation is required", "Designation");
        soft.assertEquals(form.error(EmployeeFormPage.SALARY), "Salary is required", "Salary");
        soft.assertEquals(form.error(EmployeeFormPage.JOINING_DATE), "Joining date is required", "Joining date");
        soft.assertTrue(form.isOpen(), "Form must stay open when validation fails");
        soft.assertAll();
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "No employee must be created");
    }

    @DataProvider(name = "invalidFirstNames")
    public Object[][] invalidFirstNames() {
        return new Object[][]{{"John123"}, {"<script>alert(1)</script>"}, {"J@hn"}};
    }

    @Test(dataProvider = "invalidFirstNames", groups = {"regression", "negative", "security"},
            description = "TC_VAL_002 - Invalid first name is rejected")
    public void invalidFirstNameRejected(String firstName) {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().firstName(firstName));
        Assert.assertEquals(form.error(EmployeeFormPage.FIRST_NAME), "First name must contain only letters",
                "First name error for '" + firstName + "'");
    }

    @Test(groups = {"regression", "negative"}, description = "TC_VAL_003 - Last name with digits is rejected")
    public void invalidLastNameRejected() {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().lastName("Tester99"));
        Assert.assertEquals(form.error(EmployeeFormPage.LAST_NAME), "Last name must contain only letters", "Last name error");
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][]{{"plainaddress"}, {"missing@tld"}, {"@nouser.com"}};
    }

    @Test(dataProvider = "invalidEmails", groups = {"regression", "negative"},
            description = "TC_VAL_004 - Invalid employee email is rejected")
    public void invalidEmailRejected(String email) {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().email(email));
        Assert.assertEquals(form.error(EmployeeFormPage.EMAIL), "Enter a valid email address", "Email error for " + email);
    }

    @DataProvider(name = "duplicateEmails")
    public Object[][] duplicateEmails() {
        return new Object[][]{{"john.smith@company.com"}, {"JOHN.SMITH@COMPANY.COM"}};
    }

    @Test(dataProvider = "duplicateEmails", groups = {"regression", "negative"},
            description = "TC_VAL_005 - Duplicate email is rejected (case-insensitive)")
    public void duplicateEmailRejected(String email) {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().email(email));
        Assert.assertEquals(form.error(EmployeeFormPage.EMAIL), "Email already exists", "Duplicate email error");
    }

    @DataProvider(name = "invalidPhones")
    public Object[][] invalidPhones() {
        return new Object[][]{{"12345"}, {"abcdefghij"}, {"98765432101"}, {"987654321a"}};
    }

    @Test(dataProvider = "invalidPhones", groups = {"regression", "negative"},
            description = "TC_VAL_006 - Invalid phone numbers are rejected")
    public void invalidPhoneRejected(String phone) {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().phone(phone));
        Assert.assertEquals(form.error(EmployeeFormPage.PHONE), "Phone number must be exactly 10 digits",
                "Phone error for '" + phone + "'");
    }

    @DataProvider(name = "invalidSalaries")
    public Object[][] invalidSalaries() {
        return new Object[][]{{"0"}, {"-1000"}};
    }

    @Test(dataProvider = "invalidSalaries", groups = {"regression", "negative"},
            description = "TC_VAL_007 - Zero or negative salary is rejected")
    public void invalidSalaryRejected(String salary) {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().salary(salary));
        Assert.assertEquals(form.error(EmployeeFormPage.SALARY), "Salary must be greater than 0", "Salary error for " + salary);
    }

    @Test(groups = {"regression", "negative"}, description = "TC_VAL_008 - Future joining date is rejected")
    public void futureJoiningDateRejected() {
        EmployeeFormPage form = loginAsAdmin().clickAdd();
        form.submit(TestDataFactory.validEmployee().joiningDate(LocalDate.now().plusDays(1).toString()));
        Assert.assertEquals(form.error(EmployeeFormPage.JOINING_DATE), "Joining date cannot be in the future", "Date error");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_VAL_009 - Cancel does not create an employee")
    public void cancelDoesNotCreateEmployee() {
        DashboardPage dashboard = loginAsAdmin();
        EmployeeFormPage form = dashboard.clickAdd();
        form.fill(TestDataFactory.validEmployee());
        form.cancel();
        Assert.assertFalse(form.isOpen(), "Form should be closed");
        Assert.assertEquals(dashboard.rowCount(), Constants.SEEDED_EMPLOYEE_COUNT, "Employee count must be unchanged");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_VAL_010 - Boundary values are accepted (10-digit phone, today's date)")
    public void boundaryValuesAccepted() {
        DashboardPage dashboard = loginAsAdmin();
        Employee e = TestDataFactory.validEmployee().phone("9999999999").joiningDate(LocalDate.now().toString()).salary("1");
        dashboard.clickAdd().submit(e);
        Assert.assertEquals(dashboard.toast(), "Employee added successfully", "Success toast");
        Assert.assertTrue(dashboard.hasEmployee("EMP006"), "New employee should be listed");
        Assert.assertEquals(dashboard.cellText("EMP006", "c-salary"), "1", "Minimum salary stored");
    }

    @Test(groups = {"regression", "functional"}, description = "TC_VAL_011 - Corrected data is accepted after a validation error")
    public void correctedDataAcceptedAfterError() {
        DashboardPage dashboard = loginAsAdmin();
        Employee e = TestDataFactory.validEmployee();
        String goodEmail = e.email();
        EmployeeFormPage form = dashboard.clickAdd();
        form.submit(e.email("bad-email"));
        Assert.assertEquals(form.error(EmployeeFormPage.EMAIL), "Enter a valid email address", "Initial error");
        form.setEmail(goodEmail).save();
        Assert.assertEquals(dashboard.toast(), "Employee added successfully", "Success toast after correction");
        Assert.assertTrue(form.waitUntilClosed(), "Form should close after successful save");
    }
}
