package com.example.krypt;

public class UserProfile {
    private String name;
    private String email;
    private String roll;
    private String department;
    private String contact;
    private boolean isAdmin;

    public UserProfile(String name, String email, String contact, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.isAdmin = isAdmin;
    }

    public UserProfile(String name, String email, String roll, String department, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.roll = roll;
        this.department = department;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoll() {
        return roll;
    }

    public String getDepartment() {
        return department;
    }

    public String getContact() {
        return contact;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}