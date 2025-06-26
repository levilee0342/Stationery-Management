package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.order.PurchaseOrderRequest;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PurchaseOrderService {
    List<PurchaseOrderResponse> getUserOrdersByStatus(String userId, String status);
    List<ProductDetailResponse> getProductDetailsByOrderId(String purchaseOrderId);

    PurchaseOrderResponse getPurchaseOrderDetails(String purchaseOrderId);
    Map<PurchaseOrder.Status, Long> getOrderStatusStatistics(String userId);
    void cancelOrder(String userId, String purchaseOrderId, String cancelReason);
    PurchaseOrderResponse editPurchaseOrder(String userId, String purchaseOrderId, PurchaseOrderRequest request);
    MomoResponse createOrderWithMomo(PurchaseOrderRequest request);
    MomoResponse transactionStatus(String orderId, Integer status);

    Page<PurchaseOrderResponse> getAllPendingOrders(String roleName, Pageable pageable);
    Page<PurchaseOrderResponse> getAllNonPendingOrders(String roleName, List<PurchaseOrder.Status> status, Pageable pageable);
    void confirmOrder(String purchaseOrderId);
    void updateOrderStatus(String userId, String purchaseOrderId, String status, String cancelReason);
    public void createOrderNotPayment(PurchaseOrderRequest request);
}