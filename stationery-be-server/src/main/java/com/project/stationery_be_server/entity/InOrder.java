package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class InOrder {
    @Id
    @Column(name="id")
    String orderId; // của momo

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user; // ID của người dùng

    @Column(name="payment_url")
    String paymentUrl; // URL thanh toán, có thể là của MoMo hoặc ZaloPay
    @Column(name="expried_time", nullable = false)
    LocalDateTime expiredTime; // ISO 8601 format, e.g., "2023-10-01T12:00:00Z"

}
