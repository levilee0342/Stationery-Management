package com.project.stationery_be_server.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService {
    private final UserRepository userRepository;

    @Override
    public void sendNotification(String deviceToken, String title, String message) {
        // Đây là Notification của Firebase, không phải entity của bạn
        Notification firebaseNotification = Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build();

        Message msg = Message.builder()
                .setToken(deviceToken)
                .setNotification(firebaseNotification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(msg);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            if (e.getErrorCode().equals("registration-token-not-registered") ||
                    e.getErrorCode().equals("invalid-argument")) {
                System.err.println("Invalid token: " + deviceToken + ", Error: " + e.getMessage());
                // Xóa token không hợp lệ khỏi cơ sở dữ liệu
                userRepository.findByDeviceToken(deviceToken).ifPresent(user -> {
                    user.setDeviceToken(null);
                    userRepository.save(user);
                    System.out.println("Cleared invalid device token for user: " + user.getUserId());
                });
            } else {
                e.printStackTrace();
            }
        }
    }
}

