package com.example.office_management.dto.response;


import java.io.Serializable;

public class AddressResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String addressId;

    private String addressName;

    private String phone;
    private String recipient;

    private Boolean isDefault;

    public AddressResponse() {}

    public String getAddressId() {
        return addressId;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRecipient() { return recipient; }

    public Boolean getIsDefault() {
        return isDefault;
    }

}

