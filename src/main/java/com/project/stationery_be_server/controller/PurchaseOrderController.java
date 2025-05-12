package com.project.stationery_be_server.controller;


import com.project.stationery_be_server.dto.request.order.PurchaseOrderRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;
import com.project.stationery_be_server.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;


    @PostMapping("/payment-momo")
    public ApiResponse<MomoResponse> createOrderWithMomo(@RequestBody PurchaseOrderRequest request) {
        System.out.println("Request: " + request);
        return ApiResponse.<MomoResponse>builder()
                .message("Order created successfully")
                .result(purchaseOrderService.createOrderWithMomo(request))
                .build();

    }
    @GetMapping("/payment-momo/transaction-status/{orderId}")
    public ApiResponse<MomoResponse> transactionStatus(@PathVariable String orderId) {
        return ApiResponse.<MomoResponse>builder()
                .message("Transaction status retrieved successfully")
                .result(purchaseOrderService.transactionStatus(orderId))
                .build();
    }
}
