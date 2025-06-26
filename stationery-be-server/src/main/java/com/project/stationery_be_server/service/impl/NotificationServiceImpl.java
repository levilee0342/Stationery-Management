package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.dto.response.NotificationResponse;
import com.project.stationery_be_server.entity.Notification;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.repository.NotificationRepository;
import com.project.stationery_be_server.service.FCMService;
import com.project.stationery_be_server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final FCMService firebaseMessagingService;

    private String getCurrentUserId() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String userId = authentication.getName();
        System.out.println("Authentication.getName() returns: " + userId); // Log giá trị
        return userId;
    }

    @Override
    public void notifyOrderUpdate(PurchaseOrder order) {
        String message = buildOrderStatusMessage(order);
        Notification notification = Notification.builder()
                .user(order.getUser())
                .title("Update order #" + order.getPurchaseOrderId())
                .message(message)
                .type(Notification.NotificationType.ORDER_UPDATE)
                .targetId(order.getPurchaseOrderId())
                .purchaseOrder(order)
                .build();
        notificationRepository.save(notification);

        String deviceToken = order.getUser().getDeviceToken();
        if (deviceToken != null && !deviceToken.isEmpty()) {
            firebaseMessagingService.sendNotification(
                    deviceToken,
                    "Order Update",
                    message
            );
        }
    }

    @Override
    public void notifyPromotion(UserPromotion userPromotion) {
        // 1. Danh sách các tiêu đề (titles)
        List<String> titles = Arrays.asList(
                "New Offer Just For You!",
                "Exclusive Promotion Inside!",
                "Your New Discount Awaits!",
                "Flash Sale Alert!",
                "Big Savings Are Here!",
                "Special Deal Unlocked!"
        );

        // 2. Danh sách các nội dung (messages)
        List<String> messages = Arrays.asList(
                "You have a new promotion available! Tap to reveal your exclusive discount.",
                "Good news! We've got a brand-new offer just for you. Don't miss out on these amazing savings!",
                "Unlock incredible deals with our latest promotion! Check it out now before it's gone.",
                "As a valued customer, we've prepared a special promotion exclusively for you. Click here to claim your reward!",
                "Your shopping just got better! A new promotion has been added to your account, offering fantastic discounts on your favorite items."
        );

        // 3. Chọn ngẫu nhiên một tiêu đề và một nội dung
        Random random = new Random();
        String randomTitle = titles.get(random.nextInt(titles.size()));
        String randomMessage = messages.get(random.nextInt(messages.size()));

        // 4. Tạo đối tượng Notification với các câu đã chọn ngẫu nhiên
        Notification notification = Notification.builder()
                .user(userPromotion.getUser())
                .title(randomTitle) // Tiêu đề ngẫu nhiên
                .message(randomMessage) // Nội dung ngẫu nhiên
                .type(Notification.NotificationType.PROMOTION)
                .targetId(userPromotion.getPromotion().getPromotionId())
                .userPromotion(userPromotion)
                .build();

        notificationRepository.save(notification);

        String deviceToken = userPromotion.getUser().getDeviceToken();
        if (deviceToken != null && !deviceToken.isEmpty()) {
            firebaseMessagingService.sendNotification(
                    deviceToken,
                    randomTitle,
                    randomMessage
            );
        }
    }

    @Override
    public List<NotificationResponse> getUserNotifications() {
        String userId = getCurrentUserId();
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .type(notification.getType().name())
                        .targetId(notification.getTargetId())
                        .isRead(notification.getIsRead())
                        .createdAt(notification.getCreatedAt())
                        .purchaseOrderId(
                                notification.getPurchaseOrder() != null ? notification.getPurchaseOrder().getPurchaseOrderId() : null
                        )
                        .promotionId(
                                notification.getUserPromotion() != null && notification.getUserPromotion().getPromotion() != null
                                        ? notification.getUserPromotion().getPromotion().getPromotionId()
                                        : null
                        )

                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public long countUnreadNotifications() {
        String userId = getCurrentUserId();
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }


    private String buildOrderStatusMessage(PurchaseOrder order) {
        switch (order.getStatus()) {
            case PENDING:
                return "Your order has been successfully created.";
            case PROCESSING:
                return "Your order is being processed.";
            case SHIPPING:
                return "Your order is being shipped.";
            case COMPLETED:
                return "Your order has been completed.";
            case CANCELED:
                return "Your order has been canceled.";
            default:
                return "Your order has been updated.";
        }
    }
}
