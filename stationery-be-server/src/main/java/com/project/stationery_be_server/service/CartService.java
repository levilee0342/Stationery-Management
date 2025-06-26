package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.AddCartItemRequest;
import com.project.stationery_be_server.dto.request.UpdateCartItemRequest;
import com.project.stationery_be_server.dto.response.CartResponse;

import java.util.List;

public interface CartService {
    CartResponse addItemToCart(AddCartItemRequest request);
    CartResponse updateItemInCart(String productDetailId, UpdateCartItemRequest request);
    void removeItemFromCart(String productDetailId);
    List<CartResponse> viewCart();
    List<CartResponse> viewAllCarts();
    int calculateCartTotal();
}