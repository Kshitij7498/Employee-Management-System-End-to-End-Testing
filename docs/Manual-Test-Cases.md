# Manual Test Cases — Employee Management System

Total: **73** test cases. Each case is automated; the *Automated Test* column maps to `src/test/java/com/ems/tests/`.
Test IDs also appear in the `description` of every `@Test` method and in the execution report.

**Test accounts:** `admin / Admin@123` (ADMIN) · `hr / Hr@12345` (HR) · `employee / Emp@12345` (EMPLOYEE)

**Manual execution:** open `app/index.html` in Chrome (a fresh Incognito window resets the data).

## Login

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_LOGIN_001 | Valid login for each role | High | Functional / Smoke | 1. Enter username and password<br>2. Click Login | admin/Admin@123; hr/Hr@12345; employee/Emp@12345 | Dashboard opens; role badge shows ADMIN / HR / EMPLOYEE; 'Welcome, <name>' displayed |
| TC_LOGIN_002 | Wrong password shows error | High | Negative | 1. Enter admin + wrong password<br>2. Click Login | admin / WrongPass@1 | 'Invalid username or password' shown; stays on login page |
| TC_LOGIN_003 | Unknown username shows error | Medium | Negative | 1. Enter unknown user + any password<br>2. Click Login | nosuchuser / Whatever@123 | 'Invalid username or password' shown |
| TC_LOGIN_004 | Empty credentials rejected | Medium | Negative | 1. Leave both fields blank<br>2. Click Login | - | 'Username and password are required' shown |
| TC_LOGIN_005 | Empty password rejected | Medium | Negative | 1. Enter username only<br>2. Click Login | admin / (blank) | 'Username and password are required' shown |
| TC_LOGIN_006 | Password is case sensitive | High | Negative / Security | 1. Enter admin + 'admin@123' (lower case)<br>2. Click Login | admin / admin@123 | 'Invalid username or password' shown |
| TC_LOGIN_007 | Account locks after 3 failed attempts | High | Negative / Security | 1. Submit wrong password 3 times for admin | admin / Bad@Pass1..3 | Attempts 1-2: invalid message; attempt 3: 'Account locked after 3 failed login attempts' |
| TC_LOGIN_008 | Locked account rejects correct password | High | Negative / Security | 1. Lock admin account (3 wrong attempts)<br>2. Login with correct password | admin / Admin@123 | Locked message still shown; dashboard NOT displayed |
| TC_LOGIN_009 | Password field is masked | Medium | Security | 1. Inspect password field | - | Input type is 'password' (characters masked) |
| TC_LOGIN_010 | Logout returns to login page | High | Functional | 1. Login as admin<br>2. Click Logout | admin | Login page displayed; employee data no longer visible |
| TC_LOGIN_011 | Session persists after refresh | Medium | Functional / Regression | 1. Login as admin<br>2. Refresh browser (F5) | admin | User remains logged in; role badge still ADMIN |
| TC_LOGIN_012 | Enter key submits login form | Low | Functional | 1. Type credentials<br>2. Press ENTER in password field | hr / Hr@12345 | Dashboard opens |

## Registration

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_REG_001 | Register new user and login | High | Functional / Smoke | 1. Fill all fields with valid data<br>2. Click Register<br>3. Login with new credentials | Name: Quincy Analyst; password Qa@Pass123 | 'Registration successful. Please login.' shown; login works; role = EMPLOYEE |
| TC_REG_002 | Empty form shows required errors | High | Negative | 1. Click Register with all fields blank | - | Errors for full name, email, username, password and confirm password |
| TC_REG_003 | Invalid email formats rejected | Medium | Negative / Boundary | 1. Enter invalid email<br>2. Click Register | plainaddress; user@; user@domain; @domain.com; 'user name@x.com' | 'Enter a valid email address' shown |
| TC_REG_004 | Weak passwords rejected | High | Negative / Security | 1. Enter weak password (and same confirm)<br>2. Click Register | short1!; alllowercase1!; ALLUPPERCASE1!; NoNumbers!!; NoSpecial123 | Password-strength error shown |
| TC_REG_005 | Password mismatch rejected | Medium | Negative | 1. Enter different confirm password<br>2. Click Register | Qa@Pass123 vs Different@123 | 'Passwords do not match' shown |
| TC_REG_006 | Duplicate username rejected | High | Negative | 1. Register using existing username 'admin' | admin | 'Username already exists' shown |
| TC_REG_007 | Invalid username formats rejected | Medium | Negative / Boundary | 1. Enter invalid username<br>2. Click Register | ab; 'user name'; user@123; 25-char name | 'Username must be 4-15 alphanumeric characters' shown |
| TC_REG_008 | Full name with digits rejected | Low | Negative | 1. Enter 'John123' as full name<br>2. Click Register | John123 | 'Full name must contain only letters' shown |
| TC_REG_009 | Navigate between login and registration | Low | Functional | 1. Click 'Register here'<br>2. Click 'Back to login' | - | Registration page then login page displayed |

## Employee Validation

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_VAL_001 | Empty employee form shows all required errors | High | Negative | 1. Click Save with all fields blank | - | 8 required-field errors shown; form stays open; no employee created |
| TC_VAL_002 | Invalid first name rejected | Medium | Negative / Security | 1. Enter invalid first name<br>2. Click Save | John123; <script>alert(1)</script>; J@hn | 'First name must contain only letters' shown |
| TC_VAL_003 | Last name with digits rejected | Low | Negative | 1. Enter 'Tester99' as last name<br>2. Click Save | Tester99 | 'Last name must contain only letters' shown |
| TC_VAL_004 | Invalid email rejected | Medium | Negative | 1. Enter invalid email<br>2. Click Save | plainaddress; missing@tld; @nouser.com | 'Enter a valid email address' shown |
| TC_VAL_005 | Duplicate email rejected (case-insensitive) | High | Negative | 1. Enter an existing employee's email<br>2. Click Save | john.smith@company.com; JOHN.SMITH@COMPANY.COM | 'Email already exists' shown |
| TC_VAL_006 | Invalid phone numbers rejected | Medium | Negative / Boundary | 1. Enter invalid phone<br>2. Click Save | 12345; abcdefghij; 98765432101; 987654321a | 'Phone number must be exactly 10 digits' shown |
| TC_VAL_007 | Zero / negative salary rejected | Medium | Negative / Boundary | 1. Enter 0 or -1000 as salary<br>2. Click Save | 0; -1000 | 'Salary must be greater than 0' shown |
| TC_VAL_008 | Future joining date rejected | Medium | Negative / Boundary | 1. Set joining date to tomorrow<br>2. Click Save | today + 1 day | 'Joining date cannot be in the future' shown |
| TC_VAL_009 | Cancel does not create an employee | Medium | Functional | 1. Fill valid data<br>2. Click Cancel | - | Form closes; employee count unchanged (5) |
| TC_VAL_010 | Boundary values accepted | Medium | Boundary | 1. Enter 10-digit phone, today's date and salary 1<br>2. Click Save | phone 9999999999; salary 1 | Employee EMP006 created; salary shows '1' |
| TC_VAL_011 | Corrected data accepted after error | Medium | Functional | 1. Enter invalid email<br>2. Save<br>3. Correct email<br>4. Save | bad-email -> valid email | Error first, then success toast and form closes |

## Add Employee

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_ADD_001 | Admin adds a new employee | High | Functional / Smoke | 1. Click Add Employee<br>2. Fill valid data<br>3. Save | Alex Tester, Engineering, 65000 | Toast 'Employee added successfully'; EMP006 listed with correct data; counter 'Showing 6 of 6 employees' |
| TC_ADD_002 | HR adds a new employee | High | Functional / Role | 1. Login as HR<br>2. Add valid employee | hr / Hr@12345 | Employee created successfully |
| TC_ADD_003 | Added employee persists after refresh | Medium | Regression | 1. Add employee<br>2. Refresh browser | - | Employee still listed with same data |
| TC_ADD_004 | Add form is empty when reopened | Low | Functional | 1. Open Add form<br>2. Type data<br>3. Cancel<br>4. Reopen Add form | - | All fields empty; ID shows 'Auto-generated' |

## Edit Employee

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_EDIT_001 | Edit form is pre-filled | Medium | Functional | 1. Click Edit on EMP001 | EMP001 | Title 'Edit Employee'; all fields show John Smith's data |
| TC_EDIT_002 | Update designation and salary | High | Functional / Smoke | 1. Edit EMP001<br>2. Change designation and salary<br>3. Save | Senior Software Engineer; 90000 | Toast 'Employee updated successfully'; table shows new values; no extra row |
| TC_EDIT_003 | Edited data persists after refresh | Medium | Regression | 1. Edit EMP002 designation<br>2. Save<br>3. Refresh | Head of HR | Updated designation still shown |
| TC_EDIT_004 | Duplicate email on edit rejected | High | Negative | 1. Edit EMP001<br>2. Set email to Priya's<br>3. Save | priya.sharma@company.com | 'Email already exists' shown |
| TC_EDIT_005 | Saving without email change allowed | Medium | Regression | 1. Edit EMP001<br>2. Click Save without changes | - | Update succeeds (own email not flagged as duplicate) |
| TC_EDIT_006 | Clearing a required field blocks update | High | Negative | 1. Edit EMP001<br>2. Clear first name<br>3. Save<br>4. Cancel | - | 'First name is required'; record unchanged |
| TC_EDIT_007 | Cancel edit leaves record unchanged | Medium | Functional | 1. Edit EMP001<br>2. Change values<br>3. Cancel | - | Original designation and salary retained |
| TC_EDIT_008 | HR can edit an employee | High | Functional / Role | 1. Login as HR<br>2. Edit EMP003<br>3. Save | Senior Accountant | Update succeeds |
| TC_EDIT_009 | Employee ID cannot be edited | Medium | Negative | 1. Open Edit form for EMP001 | - | Employee ID field is read-only |

## Delete Employee

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_DEL_001 | Confirmation dialog names the employee | Medium | Functional | 1. Click Delete on EMP003 | EMP003 | Dialog: 'Are you sure you want to delete employee Rahul Verma?'; employee not yet removed |
| TC_DEL_002 | Confirming deletion removes employee | High | Functional / Smoke | 1. Click Delete on EMP005<br>2. Confirm | EMP005 | Toast 'Employee deleted successfully'; row removed; 'Showing 4 of 4 employees' |
| TC_DEL_003 | Cancelling deletion keeps employee | High | Functional | 1. Click Delete on EMP002<br>2. Cancel | EMP002 | Dialog closes; employee still listed |
| TC_DEL_004 | Deleted employee not found by search | Medium | Regression | 1. Delete EMP004<br>2. Search 'Emily' | Emily | No rows; 'No employees found' |
| TC_DEL_005 | Deletion persists after refresh | Medium | Regression | 1. Delete EMP001<br>2. Refresh | EMP001 | EMP001 stays deleted; 4 rows |
| TC_DEL_006 | Delete multiple employees | Medium | Functional | 1. Delete EMP001, EMP002, EMP003 one by one | - | Only EMP004 and EMP005 remain |
| TC_DEL_007 | Newly added employee can be deleted | Medium | Functional | 1. Add employee<br>2. Delete EMP006 | - | Employee removed; back to 5 rows |

## Search

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_SRCH_001 | Search by name / email / department / ID / partial text | High | Functional / Smoke | 1. Type search term in search box | Priya->EMP002; verma->EMP003; priya.sharma@company.com->EMP002; Marketing->EMP004; EMP005; engin->EMP001 | Exactly one matching row is shown |
| TC_SRCH_002 | Search is case-insensitive | High | Functional | 1. Search 'SMITH', 'sMiTh', 'JOHN SMITH' | SMITH; sMiTh; JOHN SMITH | EMP001 returned each time |
| TC_SRCH_003 | No match shows message | Medium | Negative | 1. Search 'zzzzzz' | zzzzzz | 'No employees found'; 'Showing 0 of 5 employees' |
| TC_SRCH_004 | Clear restores full list | Medium | Functional | 1. Search 'zzzzzz'<br>2. Click Clear | - | All 5 employees shown; no-results message hidden |
| TC_SRCH_005 | Department filter | Medium | Functional | 1. Select Department = HR | HR | Only EMP002 shown |
| TC_SRCH_006 | Search + department filter combined | Medium | Functional | 1. Search 'company.com'<br>2. Filter Sales | company.com + Sales | Only EMP005; 'Showing 1 of 5 employees' |
| TC_SRCH_007 | Whitespace is trimmed | Low | Boundary | 1. Search '   priya   ' | '   priya   ' | EMP002 returned |
| TC_SRCH_008 | Special characters handled safely | Medium | Negative / Security | 1. Search special / script / SQL-like text<br>2. Click Clear | %$#@!; <script>alert(1)</script>; ' OR '1'='1 | No rows, no crash; app works after Clear |
| TC_SRCH_009 | Partial term returns multiple matches | Medium | Functional | 1. Search 'john' | john | EMP001 and EMP004 shown; 'Showing 2 of 5 employees' |
| TC_SRCH_010 | Newly added employee is searchable | Medium | Regression | 1. Add employee<br>2. Search by its email | new employee email | EMP006 returned |
| TC_SRCH_011 | Employee role can search | Low | Functional / Role | 1. Login as employee<br>2. Search 'priya' | employee / Emp@12345 | EMP002 returned |

## Role Access

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_ROLE_001 | Admin has full privileges | High | Role / Smoke | 1. Login as admin | admin | Add, Edit, Delete, Users menu and Salary column all visible |
| TC_ROLE_002 | HR can add/edit but not delete or manage users | High | Role / Smoke | 1. Login as HR | hr | Add and Edit visible; Delete and Users menu hidden; Salary visible |
| TC_ROLE_003 | Employee role is read-only | High | Role / Smoke | 1. Login as employee | employee | 5 employees visible; no Add/Edit/Delete/Users menu |
| TC_ROLE_004 | Salary hidden from Employee role | High | Role / Security | 1. Login as employee | employee | Salary column and cells not rendered |
| TC_ROLE_005 | Admin can view all user accounts | Medium | Role | 1. Login as admin<br>2. Open Users menu | admin | 3 users with roles ADMIN, HR, EMPLOYEE |
| TC_ROLE_006 | Privileges do not leak between sessions | High | Role / Security | 1. Login as admin<br>2. Logout<br>3. Login as employee | admin then employee | Employee sees no Add/Delete/Users menu |
| TC_ROLE_007 | Newly registered user is read-only | High | Role | 1. Register new user<br>2. Login | new user | Role EMPLOYEE; no Add/Edit/Delete |
| TC_ROLE_008 | Admin sees newly registered user | Medium | Role | 1. Register user<br>2. Login as admin<br>3. Open Users | new user | New user listed with role EMPLOYEE; 4 users |

## Defect Demo

| ID | Title | Priority | Type | Steps | Test Data | Expected Result |
|---|---|---|---|---|---|---|
| TC_DEMO_001 | [Injected defect] Search must be case-insensitive | High | Defect demo (fails) | 1. Open app with ?inject=search-case<br>2. Search 'SMITH' | SMITH | EXPECTED: EMP001 found. ACTUAL (injected bug): no results -> test fails |
| TC_DEMO_002 | [Injected defect] Invalid email must be rejected | Medium | Defect demo (fails) | 1. Open app with ?inject=email-validation<br>2. Add employee with email 'abc@' | abc@ | EXPECTED: validation error. ACTUAL (injected bug): saved -> test fails |
