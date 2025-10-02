package com.example.office_management.dto.request;

public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String dob;
    private String avatar;

    // Constructor, getter, setter
    public UpdateUserRequest(String firstName, String lastName, String email, String phone, String dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
    }

}
