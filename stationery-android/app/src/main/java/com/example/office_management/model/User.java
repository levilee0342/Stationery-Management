package com.example.office_management.model;

import java.util.Date;
import java.util.List;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private Date dob;
    private String avatar;
    private boolean isBlocked;
    private Integer otp;
    private Date otpCreatedAt;
    private Role role;
    private List<Address> addresses;

    public User() { }

    public User(String userId, String firstName, String lastName, String avatar) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public User(String userId, String firstName, String lastName, String email, String phone,
                String password, Date dob, String avatar, boolean isBlocked,
                Integer otp, Date otpCreatedAt, Role role,
                List<Address> addresses) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dob = dob;
        this.avatar = avatar;
        this.isBlocked = isBlocked;
        this.otp = otp;
        this.otpCreatedAt = otpCreatedAt;
        this.role = role;
        this.addresses = addresses;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Date getOtpCreatedAt() {
        return otpCreatedAt;
    }

    public void setOtpCreatedAt(Date otpCreatedAt) {
        this.otpCreatedAt = otpCreatedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
