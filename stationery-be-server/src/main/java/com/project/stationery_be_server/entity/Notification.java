package com.project.stationery_be_server.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id") // Liên kết đến User.userId
    User user;

    String title;
    String message;
    String targetId;

    @Enumerated(EnumType.STRING)
    NotificationType type;

    @Builder.Default
    Boolean isRead = false;

    @CreationTimestamp
    @Column(updatable = false)
    Date createdAt;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "user_promotion_id")
    UserPromotion userPromotion;

    public enum NotificationType {
        ORDER_UPDATE,
        PROMOTION
    }
}
