package com.ems.pages;

import com.ems.model.Employee;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class EmployeeFormPage extends BasePage {

    public static final String FIRST_NAME = "empFirstName";
    public static final String LAST_NAME = "empLastName";
    public static final String EMAIL = "empEmail";
    public static final String PHONE = "empPhone";
    public static final String DEPARTMENT = "empDepartment";
    public static final String DESIGNATION = "empDesignation";
    public static final String SALARY = "empSalary";
    public static final String JOINING_DATE = "empJoiningDate";
    public static final String ID_DISPLAY = "empIdDisplay";

    private final By modal = By.id("employeeModal");
    private final By title = By.id("modalTitle");
    private final By saveButton = By.id("saveEmployeeBtn");
    private final By cancelButton = By.id("cancelEmployeeBtn");

    public EmployeeFormPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilOpen() {
        visible(modal);
    }

    public boolean isOpen() {
        return isDisplayedNow(modal);
    }

    public boolean waitUntilClosed() {
        return waitUntilGone(modal);
    }

    public String title() {
        return text(title);
    }

    /* ---------- fields ---------- */
    public EmployeeFormPage setFirstName(String v) { type(By.id(FIRST_NAME), v, "First Name field"); return this; }
    public EmployeeFormPage setLastName(String v) { type(By.id(LAST_NAME), v, "Last Name field"); return this; }
    public EmployeeFormPage setEmail(String v) { type(By.id(EMAIL), v, "Email field"); return this; }
    public EmployeeFormPage setPhone(String v) { type(By.id(PHONE), v, "Phone field"); return this; }
    public EmployeeFormPage setDesignation(String v) { type(By.id(DESIGNATION), v, "Designation field"); return this; }
    public EmployeeFormPage setSalary(String v) { type(By.id(SALARY), v, "Salary field"); return this; }

    public EmployeeFormPage setDepartment(String v) {
        if (v != null && !v.isEmpty()) {
            selectByText(By.id(DEPARTMENT), v, "Department dropdown");
        }
        return this;
    }

    /** Date inputs are locale-dependent for typing, so the ISO value (yyyy-MM-dd) is set via JavaScript. */
    public EmployeeFormPage setJoiningDate(String isoDate) {
        setValueWithJs(By.id(JOINING_DATE), isoDate, "Joining Date field");
        return this;
    }

    public EmployeeFormPage fill(Employee e) {
        setFirstName(e.firstName());
        setLastName(e.lastName());
        setEmail(e.email());
        setPhone(e.phone());
        setDepartment(e.department());
        setDesignation(e.designation());
        setSalary(e.salary());
        setJoiningDate(e.joiningDate());
        return this;
    }

    public EmployeeFormPage save() {
        click(saveButton, "Save button");
        return this;
    }

    public EmployeeFormPage submit(Employee e) {
        fill(e);
        return save();
    }

    public void cancel() {
        click(cancelButton, "Cancel button");
        waitUntilClosed();
    }

    /* ---------- reads ---------- */
    public String error(String fieldId) {
        return waitForText(By.id(fieldId + "-error"));
    }

    public String currentError(String fieldId) {
        return currentText(By.id(fieldId + "-error"));
    }

    public String value(String fieldId) {
        return attribute(By.id(fieldId), "value");
    }

    public String selectedDepartment() {
        return new Select(visible(By.id(DEPARTMENT))).getFirstSelectedOption().getText().trim();
    }

    public boolean isReadOnly(String fieldId) {
        return visible(By.id(fieldId)).getAttribute("readonly") != null;
    }
}
