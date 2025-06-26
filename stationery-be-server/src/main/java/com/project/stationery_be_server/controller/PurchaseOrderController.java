package com.project.stationery_be_server.controller;


import com.project.stationery_be_server.dto.request.CancelOrderRequest;
import com.project.stationery_be_server.dto.request.order.PurchaseOrderRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.PurchaseOrderService;
import com.project.stationery_be_server.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/payment-momo")
    public ApiResponse<MomoResponse> createOrderWithMomo(@RequestBody PurchaseOrderRequest request) {
        System.out.println("Request: " + request);
        return ApiResponse.<MomoResponse>builder()
                .message("Tạo đơn hàng thành công")
                .result(purchaseOrderService.createOrderWithMomo(request))
                .build();

    }

    @PostMapping("/payment")
    public ApiResponse<Void> createOrderWithMomo1(@RequestBody PurchaseOrderRequest request) {
        purchaseOrderService.createOrderNotPayment(request);
        return ApiResponse.<Void>builder()
                .message("Tạo đơn hàng thành công")
                .build();

    }
    @GetMapping("/payment-momo/transaction-status/{orderId}")
    public ApiResponse<MomoResponse> transactionStatus(@PathVariable String orderId,
                                                       @RequestParam(value = "status", required = false, defaultValue = "1") Integer status) {
        return ApiResponse.<MomoResponse>builder()
                .message("Lấy trạng thái giao dịch thành công")
                .result(purchaseOrderService.transactionStatus(orderId, status))
                .build();
    }

    @GetMapping("/user/orders")
    public ApiResponse<List<PurchaseOrderResponse>> getUserOrdersByStatus(
            @RequestParam(defaultValue = "ALL") String status) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PurchaseOrderResponse> orders = purchaseOrderService.getUserOrdersByStatus(userId, status);
        String message = orders.isEmpty()
                ? "Không tìm thấy đơn hàng với trạng thái " + status.toUpperCase()
                : "Lấy danh sách đơn hàng với trạng thái " + status.toUpperCase() + " thành công";
        return ApiResponse.<List<PurchaseOrderResponse>>builder()
                .message(message)
                .result(orders)
                .build();
    }

    @GetMapping("/{purchaseOrderId}/product-details")
    public ApiResponse<List<ProductDetailResponse>> getProductDetailsByOrderId(@PathVariable String purchaseOrderId) {
        List<ProductDetailResponse> productDetails = purchaseOrderService.getProductDetailsByOrderId(purchaseOrderId);
        return ApiResponse.<List<ProductDetailResponse>>builder()
                .code(200)
                .message("Product details for order retrieved successfully")
                .result(productDetails)
                .build();
    }

    @GetMapping("/{purchaseOrderId}/purchase-details")
    public ApiResponse<PurchaseOrderResponse> getPurchaseOrderDetails(@PathVariable String purchaseOrderId) {
        PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrderDetails(purchaseOrderId);
        return ApiResponse.<PurchaseOrderResponse>builder()
                .code(200)
                .message("Purchase details for order retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping("/user/status-statistics")
    public ApiResponse<Map<PurchaseOrder.Status, Long>> getOrderStatusStatistics() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<PurchaseOrder.Status, Long> statistics = purchaseOrderService.getOrderStatusStatistics(userId);

        String message = statistics.values().stream().mapToLong(Long::longValue).sum() == 0
                ? "Không tìm thấy đơn hàng cho người dùng"
                : "Lấy thống kê trạng thái đơn hàng thành công";
        return ApiResponse.<Map<PurchaseOrder.Status, Long>>builder()
                .code(200)
                .message(message)
                .result(statistics)
                .build();
    }

    @PostMapping("/cancel/{purchaseOrderId}")
    public ApiResponse<Void> cancelOrder(@PathVariable String purchaseOrderId,
                                         @RequestBody CancelOrderRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        purchaseOrderService.cancelOrder(userId, purchaseOrderId, request.getCancelReason());
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Hủy đơn hàng thành công")
                .build();
    }

    @PutMapping("/{purchaseOrderId}")
    public ApiResponse<PurchaseOrderResponse> editPurchaseOrder(@PathVariable String purchaseOrderId,
                                                                @RequestBody PurchaseOrderRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        PurchaseOrderResponse updatedOrder = purchaseOrderService.editPurchaseOrder(userId, purchaseOrderId, request);
        return ApiResponse.<PurchaseOrderResponse>builder()
                .code(200)
                .message("Cập nhật đơn hàng thành công")
                .result(updatedOrder)
                .build();
    }
}
