package com.example.office_management.dto.request;

public class GoogleLoginRequest {
    private String token;

    public GoogleLoginRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
