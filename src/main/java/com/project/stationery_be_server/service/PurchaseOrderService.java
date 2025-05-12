package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.order.PurchaseOrderRequest;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;

public interface PurchaseOrderService {
    MomoResponse createOrderWithMomo(PurchaseOrderRequest request);
    MomoResponse transactionStatus(String orderId);
}