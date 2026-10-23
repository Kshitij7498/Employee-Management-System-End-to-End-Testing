# DEF-001: [Search] TC_DEMO_001 - [Injected defect] Search must be case-insensitive - test failed

| Field | Value |
|---|---|
| Project | EMS (Employee Management System) |
| Issue Type | Bug |
| Status | Open |
| Priority | Highest |
| Severity | Critical |
| Component | DefectDemo |
| Labels | automation, defect-demo, smoke |
| Reporter | Automation Framework (Selenium/TestNG) |
| Environment | chrome / Windows 11 / Java 17 |
| Test Reference | `com.ems.tests.DefectDemoTests#demoSearchCaseSensitivityDefect` |

## Description
[Injected defect] Search must be case-insensitive

## Steps to Reproduce
1. Enter 'admin' in Username field
2. Enter '********' in Password field
3. Click on Login button
4. Enter 'SMITH' in Search box

## Expected Result
Searching for 'SMITH' returns John Smith (EMP001).

## Actual Result
```
Search for 'SMITH' should find John Smith (case-insensitive) expected [[EMP001]] but found [[]]
```

## Attachment
Screenshot saved to `reports/run_<timestamp>/screenshots/` (embedded in the real generated report).

---
*This is a sample. Real defect files are generated automatically into `reports/run_<timestamp>/defects/` whenever a test fails.*
