/*
 * Employee Management System - application under test (AUT).
 * Plain HTML/JS, data kept in localStorage so no server or database is needed.
 *
 * Seed accounts:  admin / Admin@123   hr / Hr@12345   employee / Emp@12345
 *
 * Optional defect injection (used ONLY by the defect-report demo suite):
 *   index.html?inject=search-case        -> search becomes case-sensitive
 *   index.html?inject=email-validation   -> weak email validation
 */
(function () {
  'use strict';

  var KEYS = { users: 'ems_users', employees: 'ems_employees', failed: 'ems_failed_logins', session: 'ems_session' };
  var INJECT = new URLSearchParams(window.location.search).get('inject') || '';
  var MAX_FAILED = 3;

  var PERMS = {
    ADMIN:    { add: true,  edit: true,  del: true,  salary: true,  users: true  },
    HR:       { add: true,  edit: true,  del: false, salary: true,  users: false },
    EMPLOYEE: { add: false, edit: false, del: false, salary: false, users: false }
  };

  var NAME_RE = /^[A-Za-z]+(?:[ '\-][A-Za-z]+)*$/;
  var EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
  var USERNAME_RE = /^[A-Za-z0-9]{4,15}$/;
  var STRONG_PW_RE = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;
  var PHONE_RE = /^\d{10}$/;

  var REG_FIELDS = ['regFullName', 'regEmail', 'regUsername', 'regPassword', 'regConfirmPassword'];
  var EMP_FIELDS = ['empFirstName', 'empLastName', 'empEmail', 'empPhone', 'empDepartment', 'empDesignation', 'empSalary', 'empJoiningDate'];
  var VIEWS = ['loginView', 'registerView', 'dashboardView', 'usersView'];

  var editingId = null;
  var deleteId = null;
  var toastTimer = null;

  /* ---------- helpers ---------- */
  function $(id) { return document.getElementById(id); }
  function load(key, fallback) {
    try { var v = localStorage.getItem(key); return v ? JSON.parse(v) : fallback; } catch (e) { return fallback; }
  }
  function save(key, value) { localStorage.setItem(key, JSON.stringify(value)); }
  function pad(n) { return n < 10 ? '0' + n : '' + n; }
  function todayIso() { var d = new Date(); return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()); }
  function emailValid(v) { return INJECT === 'email-validation' ? v.indexOf('@') > 0 : EMAIL_RE.test(v); }

  function setError(id, msg) {
    var el = $(id + '-error');
    if (el) { el.textContent = msg || ''; }
    var input = $(id);
    if (input) { input.classList.toggle('invalid', !!msg); }
  }
  function applyErrors(ids, errs) {
    ids.forEach(function (id) { setError(id, errs[id] || ''); });
    return Object.keys(errs).length === 0;
  }
  function showView(id) { VIEWS.forEach(function (v) { $(v).classList.toggle('hidden', v !== id); }); }
  function showToast(msg) {
    var t = $('toast');
    t.textContent = msg;
    t.classList.remove('hidden');
    if (toastTimer) { clearTimeout(toastTimer); }
    toastTimer = setTimeout(function () { t.classList.add('hidden'); t.textContent = ''; }, 6000);
  }

  /* ---------- seed data ---------- */
  function seed() {
    if (!localStorage.getItem(KEYS.users)) {
      save(KEYS.users, [
        { username: 'admin',    password: 'Admin@123', role: 'ADMIN',    fullName: 'System Administrator', email: 'admin@company.com' },
        { username: 'hr',       password: 'Hr@12345',  role: 'HR',       fullName: 'Hannah Roberts',       email: 'hannah.roberts@company.com' },
        { username: 'employee', password: 'Emp@12345', role: 'EMPLOYEE', fullName: 'Eric Mills',           email: 'eric.mills@company.com' }
      ]);
    }
    if (!localStorage.getItem(KEYS.employees)) {
      save(KEYS.employees, [
        { empId: 'EMP001', firstName: 'John',    lastName: 'Smith',   email: 'john.smith@company.com',   phone: '9876543210', department: 'Engineering', designation: 'Software Engineer', salary: 75000, joiningDate: '2021-03-15' },
        { empId: 'EMP002', firstName: 'Priya',   lastName: 'Sharma',  email: 'priya.sharma@company.com', phone: '9123456780', department: 'HR',          designation: 'HR Manager',        salary: 68000, joiningDate: '2020-07-01' },
        { empId: 'EMP003', firstName: 'Rahul',   lastName: 'Verma',   email: 'rahul.verma@company.com',  phone: '9988776655', department: 'Finance',     designation: 'Accountant',        salary: 55000, joiningDate: '2022-01-10' },
        { empId: 'EMP004', firstName: 'Emily',   lastName: 'Johnson', email: 'emily.johnson@company.com', phone: '9012345678', department: 'Marketing',  designation: 'Marketing Lead',    salary: 62000, joiningDate: '2019-11-20' },
        { empId: 'EMP005', firstName: 'Michael', lastName: 'Brown',   email: 'michael.brown@company.com', phone: '9345678901', department: 'Sales',      designation: 'Sales Executive',   salary: 48000, joiningDate: '2023-05-05' }
      ]);
    }
  }

  /* ---------- session ---------- */
  function currentUser() {
    var name = sessionStorage.getItem(KEYS.session);
    if (!name) { return null; }
    var found = load(KEYS.users, []).filter(function (u) { return u.username === name; });
    return found.length ? found[0] : null;
  }
  function perms() { var u = currentUser(); return u ? PERMS[u.role] : PERMS.EMPLOYEE; }

  function enterApp() {
    var user = currentUser();
    if (!user) { showLogin(); return; }
    var p = PERMS[user.role];
    $('appNav').classList.remove('hidden');
    $('welcomeText').textContent = 'Welcome, ' + user.fullName;
    $('roleBadge').textContent = user.role;
    $('navUsers').classList.toggle('hidden', !p.users);
    $('addEmployeeBtn').classList.toggle('hidden', !p.add);
    $('thSalary').classList.toggle('hidden', !p.salary);
    $('thActions').classList.toggle('hidden', !(p.edit || p.del));
    showView('dashboardView');
    renderEmployees();
  }

  function showLogin() {
    $('appNav').classList.add('hidden');
    showView('loginView');
  }

  /* ---------- login / logout ---------- */
  function doLogin() {
    var username = $('loginUsername').value.trim();
    var password = $('loginPassword').value;
    $('loginError').textContent = '';
    $('loginSuccess').textContent = '';

    if (!username || !password) {
      $('loginError').textContent = 'Username and password are required';
      return;
    }
    var failed = load(KEYS.failed, {});
    var key = username.toLowerCase();
    if ((failed[key] || 0) >= MAX_FAILED) {
      $('loginError').textContent = 'Account locked after 3 failed login attempts';
      return;
    }
    var matches = load(KEYS.users, []).filter(function (u) { return u.username === username; });
    var user = matches.length ? matches[0] : null;

    if (!user) { $('loginError').textContent = 'Invalid username or password'; return; }
    if (user.password !== password) {
      failed[key] = (failed[key] || 0) + 1;
      save(KEYS.failed, failed);
      $('loginError').textContent = failed[key] >= MAX_FAILED
        ? 'Account locked after 3 failed login attempts'
        : 'Invalid username or password';
      return;
    }
    delete failed[key];
    save(KEYS.failed, failed);
    sessionStorage.setItem(KEYS.session, user.username);
    $('loginUsername').value = '';
    $('loginPassword').value = '';
    $('searchInput').value = '';
    $('departmentFilter').value = '';
    enterApp();
  }

  function doLogout() {
    sessionStorage.removeItem(KEYS.session);
    $('loginError').textContent = '';
    $('loginSuccess').textContent = '';
    showLogin();
  }

  /* ---------- registration ---------- */
  function doRegister() {
    var v = {
      regFullName: $('regFullName').value.trim(),
      regEmail: $('regEmail').value.trim(),
      regUsername: $('regUsername').value.trim(),
      regPassword: $('regPassword').value,
      regConfirmPassword: $('regConfirmPassword').value
    };
    var errs = {};
    var users = load(KEYS.users, []);

    if (!v.regFullName) { errs.regFullName = 'Full name is required'; }
    else if (!NAME_RE.test(v.regFullName)) { errs.regFullName = 'Full name must contain only letters'; }

    if (!v.regEmail) { errs.regEmail = 'Email is required'; }
    else if (!emailValid(v.regEmail)) { errs.regEmail = 'Enter a valid email address'; }

    if (!v.regUsername) { errs.regUsername = 'Username is required'; }
    else if (!USERNAME_RE.test(v.regUsername)) { errs.regUsername = 'Username must be 4-15 alphanumeric characters'; }
    else if (users.some(function (u) { return u.username.toLowerCase() === v.regUsername.toLowerCase(); })) {
      errs.regUsername = 'Username already exists';
    }

    if (!v.regPassword) { errs.regPassword = 'Password is required'; }
    else if (!STRONG_PW_RE.test(v.regPassword)) {
      errs.regPassword = 'Password must be at least 8 characters with uppercase, lowercase, number and special character';
    }

    if (!v.regConfirmPassword) { errs.regConfirmPassword = 'Confirm password is required'; }
    else if (v.regPassword !== v.regConfirmPassword) { errs.regConfirmPassword = 'Passwords do not match'; }

    if (!applyErrors(REG_FIELDS, errs)) { return; }

    users.push({ username: v.regUsername, password: v.regPassword, role: 'EMPLOYEE', fullName: v.regFullName, email: v.regEmail });
    save(KEYS.users, users);
    $('registerForm').reset();
    applyErrors(REG_FIELDS, {});
    $('loginError').textContent = '';
    $('loginSuccess').textContent = 'Registration successful. Please login.';
    showView('loginView');
  }

  /* ---------- employee list ---------- */
  function cell(text, cls) {
    var td = document.createElement('td');
    td.className = cls;
    td.textContent = text;
    return td;
  }
  function button(text, id, cls, handler) {
    var b = document.createElement('button');
    b.type = 'button';
    b.id = id;
    b.className = cls;
    b.textContent = text;
    b.addEventListener('click', handler);
    return b;
  }

  function renderEmployees() {
    var p = perms();
    var raw = $('searchInput').value.trim();
    var needle = INJECT === 'search-case' ? raw : raw.toLowerCase();
    var dept = $('departmentFilter').value;
    var all = load(KEYS.employees, []);

    var list = all.filter(function (e) {
      if (dept && e.department !== dept) { return false; }
      if (!needle) { return true; }
      var hay = [e.empId, e.firstName + ' ' + e.lastName, e.email, e.department, e.designation].join(' ');
      if (INJECT !== 'search-case') { hay = hay.toLowerCase(); }
      return hay.indexOf(needle) !== -1;
    });

    var body = $('employeeTableBody');
    body.innerHTML = '';
    list.forEach(function (e) {
      var tr = document.createElement('tr');
      tr.id = 'row-' + e.empId;
      tr.appendChild(cell(e.empId, 'c-id'));
      tr.appendChild(cell(e.firstName + ' ' + e.lastName, 'c-name'));
      tr.appendChild(cell(e.email, 'c-email'));
      tr.appendChild(cell(e.phone, 'c-phone'));
      tr.appendChild(cell(e.department, 'c-dept'));
      tr.appendChild(cell(e.designation, 'c-designation'));
      if (p.salary) { tr.appendChild(cell(Number(e.salary).toLocaleString('en-US'), 'c-salary')); }
      tr.appendChild(cell(e.joiningDate, 'c-date'));
      if (p.edit || p.del) {
        var td = document.createElement('td');
        td.className = 'c-actions';
        if (p.edit) { td.appendChild(button('Edit', 'edit-' + e.empId, 'btn-edit', function () { openForm(e.empId); })); }
        if (p.del) { td.appendChild(button('Delete', 'delete-' + e.empId, 'btn-delete', function () { openConfirm(e.empId); })); }
        tr.appendChild(td);
      }
      body.appendChild(tr);
    });

    $('employeeCount').textContent = 'Showing ' + list.length + ' of ' + all.length + ' employees';
    $('noResults').classList.toggle('hidden', list.length !== 0);
    $('employeeTable').classList.toggle('hidden', list.length === 0);
  }

  function renderUsers() {
    var users = load(KEYS.users, []);
    var body = $('usersTableBody');
    body.innerHTML = '';
    users.forEach(function (u) {
      var tr = document.createElement('tr');
      tr.id = 'user-' + u.username;
      tr.appendChild(cell(u.username, 'u-username'));
      tr.appendChild(cell(u.fullName, 'u-name'));
      tr.appendChild(cell(u.email, 'u-email'));
      tr.appendChild(cell(u.role, 'u-role'));
      body.appendChild(tr);
    });
    $('usersCount').textContent = 'Total users: ' + users.length;
  }

  /* ---------- add / edit ---------- */
  function nextEmpId() {
    var max = 0;
    load(KEYS.employees, []).forEach(function (e) {
      var n = parseInt(e.empId.replace('EMP', ''), 10);
      if (n > max) { max = n; }
    });
    var next = max + 1;
    return 'EMP' + (next < 10 ? '00' : next < 100 ? '0' : '') + next;
  }

  function openForm(empId) {
    var p = perms();
    if (empId ? !p.edit : !p.add) { return; }
    editingId = empId || null;
    applyErrors(EMP_FIELDS, {});
    $('employeeForm').reset();
    $('empIdDisplay').value = empId || 'Auto-generated';
    $('modalTitle').textContent = empId ? 'Edit Employee' : 'Add Employee';
    if (empId) {
      var e = load(KEYS.employees, []).filter(function (x) { return x.empId === empId; })[0];
      $('empFirstName').value = e.firstName;
      $('empLastName').value = e.lastName;
      $('empEmail').value = e.email;
      $('empPhone').value = e.phone;
      $('empDepartment').value = e.department;
      $('empDesignation').value = e.designation;
      $('empSalary').value = e.salary;
      $('empJoiningDate').value = e.joiningDate;
    }
    $('employeeModal').classList.remove('hidden');
  }
  function closeForm() { $('employeeModal').classList.add('hidden'); editingId = null; }

  function validateEmployee(v) {
    var errs = {};
    var emps = load(KEYS.employees, []);

    if (!v.firstName) { errs.empFirstName = 'First name is required'; }
    else if (!NAME_RE.test(v.firstName)) { errs.empFirstName = 'First name must contain only letters'; }

    if (!v.lastName) { errs.empLastName = 'Last name is required'; }
    else if (!NAME_RE.test(v.lastName)) { errs.empLastName = 'Last name must contain only letters'; }

    if (!v.email) { errs.empEmail = 'Email is required'; }
    else if (!emailValid(v.email)) { errs.empEmail = 'Enter a valid email address'; }
    else if (emps.some(function (e) { return e.email.toLowerCase() === v.email.toLowerCase() && e.empId !== editingId; })) {
      errs.empEmail = 'Email already exists';
    }

    if (!v.phone) { errs.empPhone = 'Phone number is required'; }
    else if (!PHONE_RE.test(v.phone)) { errs.empPhone = 'Phone number must be exactly 10 digits'; }

    if (!v.department) { errs.empDepartment = 'Department is required'; }
    if (!v.designation) { errs.empDesignation = 'Designation is required'; }

    if (v.salary === '') { errs.empSalary = 'Salary is required'; }
    else if (isNaN(Number(v.salary)) || Number(v.salary) <= 0) { errs.empSalary = 'Salary must be greater than 0'; }

    if (!v.joiningDate) { errs.empJoiningDate = 'Joining date is required'; }
    else if (v.joiningDate > todayIso()) { errs.empJoiningDate = 'Joining date cannot be in the future'; }
    return errs;
  }

  function saveEmployee() {
    var p = perms();
    if (editingId ? !p.edit : !p.add) { return; }
    var v = {
      firstName: $('empFirstName').value.trim(),
      lastName: $('empLastName').value.trim(),
      email: $('empEmail').value.trim(),
      phone: $('empPhone').value.trim(),
      department: $('empDepartment').value,
      designation: $('empDesignation').value.trim(),
      salary: $('empSalary').value.trim(),
      joiningDate: $('empJoiningDate').value
    };
    if (!applyErrors(EMP_FIELDS, validateEmployee(v))) { return; }

    var emps = load(KEYS.employees, []);
    var record = {
      firstName: v.firstName, lastName: v.lastName, email: v.email, phone: v.phone,
      department: v.department, designation: v.designation, salary: Number(v.salary), joiningDate: v.joiningDate
    };
    if (editingId) {
      emps = emps.map(function (e) {
        if (e.empId !== editingId) { return e; }
        record.empId = e.empId;
        return record;
      });
      showToast('Employee updated successfully');
    } else {
      record.empId = nextEmpId();
      emps.push(record);
      showToast('Employee added successfully');
    }
    save(KEYS.employees, emps);
    closeForm();
    renderEmployees();
  }

  /* ---------- delete ---------- */
  function openConfirm(empId) {
    if (!perms().del) { return; }
    var e = load(KEYS.employees, []).filter(function (x) { return x.empId === empId; })[0];
    deleteId = empId;
    $('confirmMessage').textContent = 'Are you sure you want to delete employee ' + e.firstName + ' ' + e.lastName + '?';
    $('confirmModal').classList.remove('hidden');
  }
  function closeConfirm() { $('confirmModal').classList.add('hidden'); deleteId = null; }
  function confirmDelete() {
    if (!perms().del || !deleteId) { return; }
    var id = deleteId;
    save(KEYS.employees, load(KEYS.employees, []).filter(function (e) { return e.empId !== id; }));
    closeConfirm();
    showToast('Employee deleted successfully');
    renderEmployees();
  }

  /* ---------- wiring ---------- */
  $('loginForm').addEventListener('submit', function (e) { e.preventDefault(); doLogin(); });
  $('registerForm').addEventListener('submit', function (e) { e.preventDefault(); doRegister(); });
  $('employeeForm').addEventListener('submit', function (e) { e.preventDefault(); saveEmployee(); });
  $('showRegisterLink').addEventListener('click', function () { $('loginSuccess').textContent = ''; $('loginError').textContent = ''; showView('registerView'); });
  $('showLoginLink').addEventListener('click', function () { showView('loginView'); });
  $('logoutBtn').addEventListener('click', doLogout);
  $('navEmployees').addEventListener('click', enterApp);
  $('navUsers').addEventListener('click', function () {
    var u = currentUser();
    if (!u || !PERMS[u.role].users) { return; }
    renderUsers();
    showView('usersView');
  });
  $('searchInput').addEventListener('input', renderEmployees);
  $('departmentFilter').addEventListener('change', renderEmployees);
  $('clearSearchBtn').addEventListener('click', function () {
    $('searchInput').value = '';
    $('departmentFilter').value = '';
    renderEmployees();
  });
  $('addEmployeeBtn').addEventListener('click', function () { openForm(null); });
  $('cancelEmployeeBtn').addEventListener('click', closeForm);
  $('confirmDeleteBtn').addEventListener('click', confirmDelete);
  $('cancelDeleteBtn').addEventListener('click', closeConfirm);

  seed();
  if (currentUser()) { enterApp(); } else { showLogin(); }
})();
