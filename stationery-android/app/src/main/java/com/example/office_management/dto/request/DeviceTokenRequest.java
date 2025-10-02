package com.example.office_management.dto.request;

public class DeviceTokenRequest {
    private String deviceToken;

    public DeviceTokenRequest(String deviceToken) {
        this.deviceToken = deviceToken;
    }
    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
