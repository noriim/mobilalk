package com.example.gasapplication;

public class User {
    private String name;
    private String email;
    private String address;
    private String phone;
    private String phoneType;

    public User() {}

    public User(String name, String email, String address, String phone, String phoneType) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.phoneType = phoneType;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneType() {
        return phoneType;
    }
}
