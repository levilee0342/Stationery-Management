package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.NotificationResponse;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.repository.PurchaseOrderRepository;
import com.project.stationery_be_server.repository.UserPromotionRepository;
import com.project.stationery_be_server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserPromotionRepository userPromotionRepository;

    @GetMapping()
    public ApiResponse<List<NotificationResponse>> getUserNotifications() {
        List<NotificationResponse> notifications = notificationService.getUserNotifications();
        return ApiResponse.<List<NotificationResponse>>builder().result(notifications).build();
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable String id) {
        try {
            notificationService.markAsRead(id);
            return ApiResponse.<Void>builder()
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Notification not found")
                    .build();
        }
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> countUnreadNotifications() {
        long count = notificationService.countUnreadNotifications();
        return ApiResponse.<Long>builder().result(count).build();
    }


    // Test API dùng để tạo thử notification
    @PostMapping("/order-update")
    public ApiResponse<Void> testOrderNotification(@RequestParam String purchaseOrderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(purchaseOrderId).orElseThrow();
        notificationService.notifyOrderUpdate(order);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/promotion")
    public ApiResponse<Void> testPromotionNotification(@RequestParam String userPromotionId) {
        UserPromotion userPromotion = userPromotionRepository.findById(userPromotionId).orElseThrow();
        notificationService.notifyPromotion(userPromotion);
        return ApiResponse.<Void>builder().build();
    }
}
