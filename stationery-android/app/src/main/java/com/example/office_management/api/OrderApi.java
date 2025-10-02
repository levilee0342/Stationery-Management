package com.example.office_management.api;

import com.example.office_management.dto.request.order.CancelOrderRequest;
import com.example.office_management.dto.request.order.PurchaseOrderRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.MomoResponse;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.purchaseOrder.PurchaseOrderResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {
    @POST ("purchase-orders/payment-momo")
    Call<ApiResponse<MomoResponse>> createOrderWithMomo(
            @Header("Authorization") String authHeader,
            @Body PurchaseOrderRequest request
            );

    @POST ("purchase-orders/payment")
    Call<ApiResponse<Void>> createOrder(
            @Header("Authorization") String authHeader,
            @Body PurchaseOrderRequest request
    );

    @GET("purchase-orders/payment-momo/transaction-status/{orderId}")
    Call<ApiResponse<MomoResponse>> transactionStatus(
            @Header("Authorization") String authHeader,
            @Path("orderId") String orderId,
            @Query("status") Integer status
    );

    @GET("purchase-orders/user/orders")
    Call<ApiResponse<List<PurchaseOrderResponse>>> getUserOrdersByStatus(
            @Header("Authorization") String authHeader,
            @Query("status") String status
    );

    @GET("purchase-orders/{purchaseOrderId}/product-details")
    Call<ApiResponse<List<ProductDetailResponse>>> getProductDetailsByOrderId(
            @Header("Authorization") String authHeader,
            @Path("purchaseOrderId") String purchaseOrderId
    );

    @GET("purchase-orders/{purchaseOrderId}/purchase-details")
    Call<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderDetails(
            @Header("Authorization") String authHeader,
            @Path("purchaseOrderId") String purchaseOrderId
    );

    @POST("purchase-orders/cancel/{purchaseOrderId}")
    Call<ApiResponse<Void>> cancelOrder(
            @Header("Authorization") String authHeader,
            @Path("purchaseOrderId") String purchaseOrderId,
            @Body CancelOrderRequest request
    );

    @GET("purchase-orders/user/status-statistics")
    Call<ApiResponse<Map<PurchaseOrderResponse.Status, Long>>> getOrderStatusStatistics(
            @Header("Authorization") String authHeader,
            @Query("userId") String userId
    );
}
