package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.PurchaseOrderDetail;
import com.project.stationery_be_server.entity.PurchaseOrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, String> {
    List<PurchaseOrderDetail> findByPurchaseOrder_PurchaseOrderId(String purchaseOrderId);
    long countByProductDetail_ProductDetailId(String productDetailId);
    List<PurchaseOrderDetail> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
