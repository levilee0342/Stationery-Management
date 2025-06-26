package com.project.stationery_be_server.service;

public interface FCMService {
    void sendNotification(String deviceToken, String title, String message);
}
