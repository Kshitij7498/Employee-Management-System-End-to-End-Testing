package com.ems.pages;

import com.ems.utils.StepLog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class DashboardPage extends BasePage {

    private final By view = By.id("dashboardView");
    private final By welcome = By.id("welcomeText");
    private final By roleBadge = By.id("roleBadge");
    private final By logoutButton = By.id("logoutBtn");
    private final By searchInput = By.id("searchInput");
    private final By departmentFilter = By.id("departmentFilter");
    private final By clearButton = By.id("clearSearchBtn");
    private final By addButton = By.id("addEmployeeBtn");
    private final By rows = By.cssSelector("#employeeTableBody tr");
    private final By count = By.id("employeeCount");
    private final By noResults = By.id("noResults");
    private final By toast = By.id("toast");
    private final By salaryHeader = By.id("thSalary");
    private final By usersNav = By.id("navUsers");
    private final By employeesNav = By.id("navEmployees");
    private final By confirmModal = By.id("confirmModal");
    private final By confirmMessage = By.id("confirmMessage");
    private final By confirmDeleteButton = By.id("confirmDeleteBtn");
    private final By cancelDeleteButton = By.id("cancelDeleteBtn");
    private final By usersRows = By.cssSelector("#usersTableBody tr");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /* ---------- state ---------- */
    public boolean isLoaded() {
        return waitUntilDisplayed(view);
    }

    public DashboardPage waitUntilLoaded() {
        visible(view);
        return this;
    }

    public boolean isHidden() {
        return !isDisplayedNow(view);
    }

    public String welcomeText() {
        return text(welcome);
    }

    public String roleBadge() {
        return text(roleBadge);
    }

    public LoginPage logout() {
        click(logoutButton, "Logout button");
        LoginPage login = new LoginPage(driver);
        login.isDisplayed();
        return login;
    }

    /* ---------- search & filter ---------- */
    public DashboardPage search(String term) {
        type(searchInput, term, "Search box");
        return this;
    }

    public DashboardPage clearSearch() {
        click(clearButton, "Clear button");
        return this;
    }

    public DashboardPage filterDepartment(String department) {
        selectByText(departmentFilter, department, "Department filter");
        return this;
    }

    /* ---------- table ---------- */
    public int rowCount() {
        return driver.findElements(rows).size();
    }

    public List<String> visibleEmployeeIds() {
        List<String> ids = new ArrayList<>();
        for (WebElement row : driver.findElements(rows)) {
            ids.add(row.findElement(By.cssSelector(".c-id")).getText().trim());
        }
        return ids;
    }

    public boolean hasEmployee(String empId) {
        return !driver.findElements(By.id("row-" + empId)).isEmpty();
    }

    /** col: c-id, c-name, c-email, c-phone, c-dept, c-designation, c-salary, c-date */
    public String cellText(String empId, String col) {
        return text(By.cssSelector("#row-" + empId + " ." + col));
    }

    public String countText() {
        return text(count);
    }

    public boolean isNoResultsDisplayed() {
        return isDisplayedNow(noResults);
    }

    public String noResultsText() {
        return currentText(noResults);
    }

    public boolean hasEmployeeSalaryCell(String empId) {
        return !driver.findElements(By.cssSelector("#row-" + empId + " .c-salary")).isEmpty();
    }

    public boolean isSalaryColumnVisible() {
        return isDisplayedNow(salaryHeader);
    }

    /* ---------- toast ---------- */
    public String toast() {
        return waitForText(toast);
    }

    /* ---------- add / edit ---------- */
    public boolean isAddVisible() {
        return isDisplayedNow(addButton);
    }

    public boolean isEditVisible(String empId) {
        return isDisplayedNow(By.id("edit-" + empId));
    }

    public boolean isDeleteVisible(String empId) {
        return isDisplayedNow(By.id("delete-" + empId));
    }

    public EmployeeFormPage clickAdd() {
        click(addButton, "Add Employee button");
        EmployeeFormPage form = new EmployeeFormPage(driver);
        form.waitUntilOpen();
        return form;
    }

    public EmployeeFormPage clickEdit(String empId) {
        click(By.id("edit-" + empId), "Edit button of " + empId);
        EmployeeFormPage form = new EmployeeFormPage(driver);
        form.waitUntilOpen();
        return form;
    }

    /* ---------- delete ---------- */
    public DashboardPage clickDelete(String empId) {
        click(By.id("delete-" + empId), "Delete button of " + empId);
        visible(confirmModal);
        return this;
    }

    public String confirmMessage() {
        return text(confirmMessage);
    }

    public boolean isConfirmDisplayed() {
        return isDisplayedNow(confirmModal);
    }

    public DashboardPage confirmDelete() {
        click(confirmDeleteButton, "Confirm Delete button");
        waitUntilGone(confirmModal);
        return this;
    }

    public DashboardPage cancelDelete() {
        click(cancelDeleteButton, "Cancel button in delete dialog");
        waitUntilGone(confirmModal);
        return this;
    }

    /* ---------- users page (admin) ---------- */
    public boolean isUsersNavVisible() {
        return isDisplayedNow(usersNav);
    }

    public DashboardPage openUsers() {
        click(usersNav, "Users menu");
        visible(By.id("usersView"));
        return this;
    }

    public DashboardPage backToEmployees() {
        click(employeesNav, "Employees menu");
        waitUntilLoaded();
        return this;
    }

    public int usersRowCount() {
        return driver.findElements(usersRows).size();
    }

    public boolean hasUser(String username) {
        return !driver.findElements(By.id("user-" + username)).isEmpty();
    }

    public String userRole(String username) {
        return text(By.cssSelector("#user-" + username + " .u-role"));
    }

    public void logStep(String message) {
        StepLog.add(message);
    }
}
