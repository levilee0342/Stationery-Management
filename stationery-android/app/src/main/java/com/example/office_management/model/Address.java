package com.example.office_management.model;

public class Address {
    private String addressId;
    private String addressName;
    private String phone;
    private boolean isDefault;
    private String userId;

    public Address() {
    }

    public Address(String addressId, String addressName, String phone, boolean isDefault, String userId) {
        this.addressId = addressId;
        this.addressName = addressName;
        this.phone = phone;
        this.isDefault = isDefault;
        this.userId = userId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

