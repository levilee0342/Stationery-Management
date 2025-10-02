package com.example.office_management.dto.response.auth;

import com.example.office_management.dto.response.UserResponse;

public class LoginGoogleResponse {
    private String accessToken;
    private UserResponse userData;
    private boolean isNewUser;

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserResponse getUserData() {
        return userData;
    }

    public void setUserData(UserResponse userData) {
        this.userData = userData;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }
}
