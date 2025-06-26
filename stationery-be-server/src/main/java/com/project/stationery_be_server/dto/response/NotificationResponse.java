package com.project.stationery_be_server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    String title;
    String message;
    String type;
    Boolean isRead;
    Date createdAt;
    String targetId;
    String promotionId;
    String purchaseOrderId;
}
