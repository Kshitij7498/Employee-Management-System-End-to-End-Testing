# Employee Management System — End-to-End Testing

Selenium WebDriver · Java · TestNG · Maven · Jira-style defect reports

A complete, ready-to-run QA project: a bundled **Employee Management System** web app (the application under test)
plus a **Page-Object based automation framework** covering registration, login, form validation, search, edit,
delete and role-based access — with manual test cases, regression suites, screenshots on failure, an HTML execution
report and auto-generated Jira-style defect reports.

> **No code changes needed.** The app is included in `/app`, drivers are downloaded automatically by Selenium Manager,
> and all dependencies come from Maven.

---

## 1. Prerequisites

| Tool | Version | Check |
|---|---|---|
| JDK | 11 or newer (17 recommended) | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Google Chrome | latest (Firefox / Edge also supported) | |
| VS Code | + *Extension Pack for Java* | extensions are suggested automatically |

Internet is needed the first time only (Maven dependencies + browser driver download).

## 2. Run in VS Code (3 steps)

1. **File → Open Folder…** → select this project folder. Accept the *"Install recommended extensions"* prompt and wait for Java to finish importing the Maven project (bottom-right status bar).
2. Open **Terminal → New Terminal** and run:
   ```bash
   mvn clean test
   ```
   A Chrome window opens and runs the whole suite (~101 test executions).
3. Open the report: **`reports/latest-report.html`** (right-click → *Reveal in File Explorer* → double-click to open it in your browser).

Alternative: `Terminal → Run Task…` and choose an **EMS:** task, or use the Testing (flask) panel to run any test class/method directly.

## 3. Run options

| Goal | Command |
|---|---|
| Full suite | `mvn clean test` |
| Smoke only | `mvn clean test -DsuiteXmlFile=testng-smoke.xml` |
| Regression | `mvn clean test -DsuiteXmlFile=testng-regression.xml` |
| Negative tests | `mvn clean test -DsuiteXmlFile=testng-negative.xml` |
| No browser window (CI) | `mvn clean test -Dheadless=true` |
| Another browser | `mvn clean test -Dbrowser=firefox` (or `edge`) |
| **Defect-report demo** (fails on purpose) | `mvn clean test -DsuiteXmlFile=testng-defect-demo.xml` |
| One test class | `mvn test -Dtest=LoginTests` *(or run it from the VS Code Testing panel)* |

Shortcuts: `./run-tests.sh smoke` (Mac/Linux) · `run-tests.bat smoke` (Windows).
Defaults live in `src/test/resources/config.properties` (any value can be overridden with `-Dkey=value`).

## 4. What is tested (71 test cases + 2 defect demos)

| Module | Class | Cases | Highlights |
|---|---|---|---|
| Login | `LoginTests` | 12 | all 3 roles, wrong/empty/case-sensitive password, **lockout after 3 attempts**, session, logout |
| Registration | `RegistrationTests` | 9 | success + login, required fields, invalid emails, weak passwords, mismatch, duplicate/invalid username |
| Form validation | `EmployeeValidationTests` | 11 | required fields, name/email/phone/salary/date rules, duplicate email, XSS input, boundary values |
| Add | `AddEmployeeTests` | 4 | Admin & HR add, persistence after refresh |
| Edit | `EditEmployeeTests` | 9 | pre-fill, update, persistence, duplicate email, read-only ID, cancel |
| Delete | `DeleteEmployeeTests` | 7 | confirm dialog, confirm/cancel, persistence, multiple deletes |
| Search | `SearchTests` | 11 | by name/email/dept/ID, case-insensitive, partial, filter + search, special characters |
| Role access | `RoleAccessTests` | 8 | Admin / HR / Employee privileges, salary masking, privilege leakage, new-user role |
| Defect demo | `DefectDemoTests` | 2 | intentionally failing (excluded from normal runs) |

Test groups: `smoke`, `regression`, `functional`, `negative`, `role`, `security`, `defect-demo`.
Full case list with steps/expected results: **`docs/Manual-Test-Cases.md`** and **`.csv`** (open in Excel). Test IDs (e.g. `TC_LOGIN_007`) match the automated `@Test(description=…)`.

## 5. Application under test

`app/index.html` is a self-contained web app (data in browser `localStorage`, nothing to install or start).
You can also open it manually in Chrome for exploratory/manual testing.

| Account | Password | Role | Can do |
|---|---|---|---|
| `admin` | `Admin@123` | ADMIN | view, add, edit, **delete**, manage users, see salary |
| `hr` | `Hr@12345` | HR | view, add, edit, see salary |
| `employee` | `Emp@12345` | EMPLOYEE | view only, **no salary** |

Business rules: password = 8+ chars with upper, lower, digit, special · account locks after 3 failed logins ·
unique email per employee · phone = 10 digits · salary > 0 · joining date not in the future · new registrations get EMPLOYEE role.

Every test starts a **fresh browser profile**, so the app re-seeds itself (5 employees EMP001–EMP005) and tests never depend on each other.

## 6. Reports & defects (generated on every run)

```
reports/
├── latest-report.html                 ← always redirects to the newest run
└── run_20261023_101500/
    ├── ExecutionReport.html           ← ExtentReports dashboard (pass/fail, steps, categories, screenshots)
    ├── screenshots/                   ← PNG captured automatically for each failure
    └── defects/
        ├── DEF-001_<test>.md          ← Jira-style defect (summary, priority, severity, steps, expected/actual, screenshot, stack trace)
        └── jira-import.csv            ← one row per defect → Jira "Import issues from CSV"
```

* **Steps to reproduce** are built automatically from the UI actions the test performed (passwords are masked).
* **Priority / severity** are derived from the test group (smoke → Highest/Critical, role/security → High/Major, negative → Medium/Moderate).
* TestNG's own reports are also in `target/surefire-reports/`.
* Try it: `mvn clean test -DsuiteXmlFile=testng-defect-demo.xml` → opens the app with a deliberately injected bug, fails, and produces a screenshot + `DEF-001`/`DEF-002`. A sample is in `docs/sample-defects/`.

## 7. Project structure

```
├── pom.xml                     Maven (Selenium 4.25, TestNG 7.10, ExtentReports 5.1)
├── testng*.xml                 Suites: full, smoke, regression, negative, defect-demo
├── app/                        Application under test (HTML/CSS/JS)
├── docs/                       Test plan, manual test cases, defect template, sample defect
├── src/test/resources/config.properties
└── src/test/java/com/ems/
    ├── base/BaseTest.java      Driver lifecycle (new browser per test)
    ├── config/ConfigReader     config.properties + -D overrides
    ├── driver/DriverFactory    Chrome / Firefox / Edge (+ headless)
    ├── pages/                  Page Objects: Login, Register, Dashboard, EmployeeForm
    ├── model/Employee          Test-data model
    ├── listeners/TestListener  Report + screenshot + defect on failure
    ├── utils/                  ExtentManager, ScreenshotUtil, DefectReportGenerator, StepLog, TestDataFactory …
    └── tests/                  8 test classes + defect demo
```

## 8. Troubleshooting

| Problem | Fix |
|---|---|
| `release version 11 not supported` / `invalid target` | You are on JDK 8 — install JDK 11+ and set `JAVA_HOME`. |
| Driver errors / browser not found | Install Chrome, or use `-Dbrowser=edge` / `firefox`. Corporate proxy? Configure Maven/Java proxy so Selenium Manager can download the driver. |
| VS Code shows red squiggles | Run *Java: Clean Java Language Server Workspace* → Reload. |
| `mvn` not recognised | Install Maven and add `bin` to `PATH`, or use the VS Code *Maven* sidebar. |
| Tests can't find the app | Run from the project root (folder containing `app/`). The framework also searches parent folders. |
| Want to test a hosted copy of the app | Set `app.url=https://…` in `config.properties`. |

## 9. Push to GitHub

```bash
git init
git add .
git commit -m "Employee Management System - end-to-end testing framework"
git branch -M main
git remote add origin https://github.com/Kshitij7498/Employee-Management-System-End-to-End-Testing.git
git push -u origin main
```
(`reports/` and `target/` are git-ignored; commit a sample report or screenshots manually if you want them in the repo.)

## 10. Resume bullets

* Designed an end-to-end **Selenium/Java/TestNG** automation framework (Page Object Model, data-driven tests) with **71 test cases / 100+ executions** covering registration, login, validation, search, CRUD and **role-based access**.
* Built functional, negative, boundary and security-oriented tests (account lockout, XSS/SQL-like input, salary masking, privilege-leakage checks) plus **smoke and regression suites** run through Maven.
* Implemented **screenshot-on-failure**, ExtentReports execution reports and an **automatic Jira-style defect generator** (reproduction steps, priority/severity, Jira CSV import).
* Authored a test plan, 73 documented manual test cases with traceability to automation, and a defect-report template.
