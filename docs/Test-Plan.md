# Test Plan — Employee Management System (End-to-End)

## 1. Objective
Verify the business-critical workflows of the Employee Management System: registration, login, employee CRUD,
search, form validation and role-based access control, using automated Selenium tests plus documented manual cases.

## 2. Scope
| In scope | Out of scope |
|---|---|
| Registration, login, logout, lockout, session | Performance / load testing |
| Add / edit / delete employee | Cross-device mobile testing |
| Form validation (positive, negative, boundary) | Database / API layer |
| Search and department filter | Email notifications |
| Role-based access (Admin / HR / Employee) | |

## 3. Test Types
| Type | How it is covered |
|---|---|
| Functional | Happy-path tests (`functional` group) |
| Negative | Invalid input, wrong credentials, lockout (`negative` group) |
| Boundary | 10-digit phone, salary = 1, joining date = today, username length |
| Security-oriented | XSS/SQL-like input, password masking, salary hidden, privilege leakage (`security` group) |
| Role-based | Admin vs HR vs Employee privileges (`role` group) |
| Smoke | Critical-path subset (`smoke` group) — `testng-smoke.xml` |
| Regression | Full automated set (`regression` group) — `testng-regression.xml` |

## 4. Test Environment
Chrome (default), Firefox or Edge · Java 11+ · Maven 3.8+ · Selenium 4.25 · TestNG 7.10 · ExtentReports 5.1.
Application under test: bundled static web app in `/app` (data in browser localStorage).

## 5. Test Data Strategy
* Seeded accounts and 5 seeded employees (EMP001–EMP005) are created automatically on first load.
* Each test starts a new browser session (fresh profile) → no dependency between tests.
* Unique emails/usernames are generated per run (`TestDataFactory`).

## 6. Entry / Exit Criteria
* Entry: application build available, Java/Maven/Chrome installed.
* Exit: 100% of smoke tests pass; ≥ 95% of regression passes; no open Critical/Major defects.

## 7. Defect Management
Every failure automatically produces a screenshot, an Extent report entry and a Jira-style defect file
(`reports/run_*/defects/DEF-nnn_*.md`) plus `jira-import.csv` for bulk import.
Priority mapping: smoke → Highest/Critical · role/security → High/Major · negative → Medium/Moderate · other → Medium/Minor.

## 8. Deliverables
Manual test cases (CSV + MD) · Automation framework · Execution report (HTML) · Defect reports · Screenshots.
