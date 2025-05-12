package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PurchaseOrder {
    @Id
    @Column(name = "purchase_order_id")
    private String purchaseOrderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<PurchaseOrderDetail> purchaseOrderDetails;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_promotion_id")
    @JsonBackReference
    private UserPromotion userPromotion;

    @Column(name = "amount", precision = 19, scale = 4)
    private Long amount;

    @Column(name="note", length = 100)
    private String note;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private Payment payment;
    public enum Status {
        PENDING,        // Chờ xác nhận
        PROCESSING,     // Đang xử lý
        SHIPPING,       // Đang giao
        COMPLETED,      // Hoàn thành
        CANCELED        // Đã hủy
    }
}
