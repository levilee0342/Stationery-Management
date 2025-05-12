package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    Optional<PurchaseOrder> findByPurchaseOrderId(String purchaseOrderId);
}