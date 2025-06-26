package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.AddCartItemRequest;
import com.project.stationery_be_server.dto.request.UpdateCartItemRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.CartResponse;
import com.project.stationery_be_server.dto.response.SizeResponse;
import com.project.stationery_be_server.entity.Review;
import com.project.stationery_be_server.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. Thêm hoặc tạo giỏ hàng
    @PostMapping
    public ApiResponse<CartResponse> addItemToCart(@RequestBody AddCartItemRequest request) {
        CartResponse cartResponse = cartService.addItemToCart(request);
        return ApiResponse.<CartResponse>builder()
                .result(cartResponse)
                .message("Add item successfully")
                .build();
    }

    // 2. Cập nhật item trong giỏ hàng
    @PutMapping("/{productDetailId}")
    public ApiResponse<CartResponse> updateItemInCart(
            @PathVariable String productDetailId,
            @RequestBody UpdateCartItemRequest request) {
        CartResponse cartResponse = cartService.updateItemInCart(productDetailId, request);
        return ApiResponse.<CartResponse>builder()
                .result(cartResponse)
                .message("Update item successfully")
                .build();
    }

    // 3. Xóa item khỏi giỏ hàng
    @DeleteMapping("/{productDetailId}")
    public ApiResponse<Void> removeItemFromCart(@PathVariable String productDetailId) {
        cartService.removeItemFromCart(productDetailId);
        return ApiResponse.<Void>builder()
                .message("Item removed successfully")
                .build();
    }
    // 4. Xem giỏ hàng của user
    @GetMapping
    public ApiResponse<List<CartResponse>> viewCart() {
        List<CartResponse> cartResponses = cartService.viewCart();
        return ApiResponse.<List<CartResponse> >builder()
                .message("Get cart successfully")
                .result(cartResponses)
                .build();
    }

    // 5. Xem tất cả giỏ hàng (admin)
    @GetMapping("/all")
    public ApiResponse<List<CartResponse>> viewAllCarts() {
        List<CartResponse> cartResponses = cartService.viewAllCarts();
        return ApiResponse.<List<CartResponse> >builder()
                .message("Get all cart successfully")
                .result(cartResponses)
                .build();
    }
}