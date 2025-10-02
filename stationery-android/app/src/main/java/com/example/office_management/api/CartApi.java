package com.example.office_management.api;

import java.util.List;

import com.example.office_management.dto.request.cart.AddCartRequest;
import com.example.office_management.dto.request.cart.UpdateCartRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {
    @GET("carts")
    Call<ApiResponse<List<CartResponse>>> viewCart(@Header("Authorization") String authHeader);

    @POST("carts")
    Call<ApiResponse<CartResponse>> addItemCart(
            @Header("Authorization") String authHeader,
            @Body AddCartRequest request
    );

    @PUT("carts/{productDetailId}")
    Call<ApiResponse<CartResponse>> updateItemCart(
            @Header("Authorization") String authHeader,
            @Path("productDetailId") String productDetailId,
            @Body UpdateCartRequest request
    );
    @DELETE("carts/{productDetailId}")
    Call<ApiResponse<CartResponse>> removeItemCart(
            @Header("Authorization") String authHeader,
            @Path("productDetailId") String productDetailId
    );

}
