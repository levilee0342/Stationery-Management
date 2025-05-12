package com.project.stationery_be_server.entity;

import com.project.stationery_be_server.listener.ProductEntityListener;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(ProductEntityListener.class)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    String paymentId;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    String payType;
    Integer status;
    String payName;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    String createdAt;

}
