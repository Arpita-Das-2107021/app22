// Admin.java
package com.example.krypt;

public class Admin {
    private String name;
    private String email;
    private String contact;

    public Admin(String name, String email, String contact) {
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }
}