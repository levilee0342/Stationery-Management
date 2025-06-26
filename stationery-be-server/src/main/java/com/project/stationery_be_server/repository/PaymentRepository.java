package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Payment;
import com.project.stationery_be_server.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
