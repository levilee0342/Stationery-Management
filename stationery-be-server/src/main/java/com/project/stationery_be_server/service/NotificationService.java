package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.response.NotificationResponse;
import com.project.stationery_be_server.entity.Notification;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.UserPromotion;

import java.util.List;

public interface NotificationService {
    void notifyOrderUpdate(PurchaseOrder order);
    void notifyPromotion(UserPromotion userPromotion);
    List<NotificationResponse> getUserNotifications();
    void markAsRead(String notificationId);
    long countUnreadNotifications();

}

