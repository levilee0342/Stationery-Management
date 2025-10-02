package com.example.office_management.dto.response;

import com.example.office_management.dto.response.search.SearchHistoryResponse;

import java.util.Date;
import java.util.Set;

public class UserResponse {
    private String userId;
    private String avatar;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dob;

    private Set<SearchHistoryResponse> searchHistory;

    public UserResponse() {}

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Date getDob() {
        return dob;
    }
    public Set<SearchHistoryResponse> getSearchHistory() {
        return searchHistory;
    }
}