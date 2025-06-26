package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.UpdateStatusRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.PurchaseOrderResponse;
import com.project.stationery_be_server.dto.response.UserResponse;
import com.project.stationery_be_server.entity.PurchaseOrder;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.PurchaseOrderService;
import com.project.stationery_be_server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final UserRepository userRepository;
    private final UserService userService;

    public AdminOrderController(PurchaseOrderService purchaseOrderService, UserRepository userRepository,
                                UserService userService) {
        this.purchaseOrderService = purchaseOrderService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/pending")
    public ApiResponse<Page<PurchaseOrderResponse>> getAllPendingOrders(
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String authUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userRepository.existsById(authUserId)) {
            throw new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
        }
        UserResponse user = userService.getUserInfo();
        if (!user.getRole().getRoleId().equals("111")) {
            throw new AppException(NotExistedErrorCode.USER_NOT_ADMIN);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PurchaseOrderResponse> orders = purchaseOrderService.getAllPendingOrders(roleName, pageable);
        String message = orders.isEmpty()
                ? "Không có đơn hàng đang chờ xử lý"
                : "Lấy danh sách đơn hàng đang chờ xử lý thành công";
        return ApiResponse.<Page<PurchaseOrderResponse>>builder()
                .code(200)
                .message(message)
                .result(orders)
                .build();
    }

    @GetMapping("/non-pending")
    public ApiResponse<Page<PurchaseOrderResponse>> getAllNonPendingOrders(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) List<PurchaseOrder.Status> status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String authUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userRepository.existsById(authUserId)) {
            throw new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
        }
        UserResponse user = userService.getUserInfo();
        if (!user.getRole().getRoleId().equals("111")) {
            throw new AppException(NotExistedErrorCode.USER_NOT_ADMIN);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PurchaseOrderResponse> orders = purchaseOrderService.getAllNonPendingOrders(roleName, status, pageable);
        String message = orders.isEmpty()
                ? "Không có đơn hàng đã xử lý"
                : "Lấy danh sách đơn hàng đã xử lý thành công";
        return ApiResponse.<Page<PurchaseOrderResponse>>builder()
                .code(200)
                .message(message)
                .result(orders)
                .build();
    }

    @PostMapping("/confirm/{purchaseOrderId}")
    public ApiResponse<Void> confirmOrder(@PathVariable String purchaseOrderId) {
        String authUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userRepository.existsById(authUserId)) {
            throw new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
        }
        UserResponse user = userService.getUserInfo();
        if (!user.getRole().getRoleId().equals("111")) {
            throw new AppException(NotExistedErrorCode.USER_NOT_ADMIN);
        }

        purchaseOrderService.confirmOrder(purchaseOrderId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xác nhận đơn hàng thành công")
                .build();
    }

    @PutMapping("/update-status/{purchaseOrderId}")
    public ApiResponse<Void> updateOrderStatus(
            @PathVariable String purchaseOrderId,
            @RequestBody UpdateStatusRequest request) {
        String authUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userRepository.existsById(authUserId)) {
            throw new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
        }
        UserResponse user = userService.getUserInfo();
        if (!user.getRole().getRoleId().equals("111")) {
            throw new AppException(NotExistedErrorCode.USER_NOT_ADMIN);
        }

        purchaseOrderService.updateOrderStatus(authUserId, purchaseOrderId, request.getStatus(), request.getCancelReason());
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Cập nhật trạng thái đơn hàng thành công")
                .build();
    }
}