package com.ems.model;

/** Simple test-data holder for the employee form. All values are Strings so invalid data can be tested. */
public class Employee {

    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String phone = "";
    private String department = "";
    private String designation = "";
    private String salary = "";
    private String joiningDate = "";

    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public String email() { return email; }
    public String phone() { return phone; }
    public String department() { return department; }
    public String designation() { return designation; }
    public String salary() { return salary; }
    public String joiningDate() { return joiningDate; }

    public Employee firstName(String v) { this.firstName = v; return this; }
    public Employee lastName(String v) { this.lastName = v; return this; }
    public Employee email(String v) { this.email = v; return this; }
    public Employee phone(String v) { this.phone = v; return this; }
    public Employee department(String v) { this.department = v; return this; }
    public Employee designation(String v) { this.designation = v; return this; }
    public Employee salary(String v) { this.salary = v; return this; }
    public Employee joiningDate(String v) { this.joiningDate = v; return this; }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
