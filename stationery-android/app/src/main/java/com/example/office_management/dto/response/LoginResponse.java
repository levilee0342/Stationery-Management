package com.example.office_management.dto.response;

public class LoginResponse {
    private int code;
    private String message;
    private ResultData result;

    public String getAccessToken() {
        return result != null ? result.getAccessToken() : null;
    }

    public ResultData getResult() {
        return result;
    }

    public static class ResultData {
        private String accessToken;
        private UserData userData;

        // Getter và setter
        public String getAccessToken() {
            return accessToken;
        }

        public UserData getUserData() {
            return userData;
        }
    }

    public static class UserData {
        private String userId;

        public String getUserId() {
            return userId;
        }
    }
}