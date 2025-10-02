package com.example.office_management.dto.request;

public class AddressRequest {
    private String addressId;
    private String addressName;
    private String phone;
    private String recipient;
    private Boolean isDefault;

    public AddressRequest(String addressId, String addressName, String phone, String recipient, Boolean isDefault) {
        this.addressId = addressId;
        this.addressName = addressName;
        this.phone = phone;
        this.recipient = recipient;
        this.isDefault = isDefault;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRecipient() {
        return recipient;
    }
    public Boolean getIsDefault() {
        return isDefault;
    }
}
