package com.ems.tests;

import com.ems.base.BaseTest;
import com.ems.model.Employee;
import com.ems.pages.DashboardPage;
import com.ems.pages.EmployeeFormPage;
import com.ems.utils.AppLocator;
import com.ems.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

/**
 * INTENTIONAL FAILURES - excluded from the normal suites.
 * Run with:  mvn test -DsuiteXmlFile=testng-defect-demo.xml
 * The bundled app is opened with ?inject=... which switches on a deliberate bug, so you can see
 * the failure screenshot, the Extent report entry and the generated Jira-style defect files.
 */
public class DefectDemoTests extends BaseTest {

    private String injectedDefect = "search-case";

    @Override
    protected String startUrl() {
        return AppLocator.url("inject=" + injectedDefect);
    }

    @Test(groups = {"defect-demo", "smoke"}, description = "TC_DEMO_001 - [Injected defect] Search must be case-insensitive")
    public void demoSearchCaseSensitivityDefect() {
        injectedDefect = "search-case";
        driver.get(startUrl());
        DashboardPage dashboard = loginAsAdmin();
        dashboard.search("SMITH");
        Assert.assertEquals(dashboard.visibleEmployeeIds(), Collections.singletonList("EMP001"),
                "Search for 'SMITH' should find John Smith (case-insensitive)");
    }

    @Test(groups = {"defect-demo", "negative"}, description = "TC_DEMO_002 - [Injected defect] Invalid email must be rejected")
    public void demoEmailValidationDefect() {
        injectedDefect = "email-validation";
        driver.get(startUrl());
        DashboardPage dashboard = loginAsAdmin();
        Employee e = TestDataFactory.validEmployee().email("abc@");
        EmployeeFormPage form = dashboard.clickAdd();
        form.submit(e);
        Assert.assertEquals(form.error(EmployeeFormPage.EMAIL), "Enter a valid email address",
                "Email 'abc@' should be rejected");
    }
}
