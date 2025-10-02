package com.example.office_management.api;

import com.example.office_management.dto.request.AddressRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.AddressResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressApi {
    @GET("address")
    Call<ApiResponse<List<AddressResponse>>> getAllMyAddresses(@Header("Authorization") String authHeader);

    @POST("address")
    Call<ApiResponse<AddressResponse>> createAddress(
            @Header("Authorization") String authHeader,
            @Body AddressRequest request
    );

    @PUT("address/{id}")
    Call<ApiResponse<AddressResponse>> updateAddress(
            @Header("Authorization") String authHeader,
            @Path("id") String id,
            @Body AddressRequest request
    );

    @PUT("address/default/{id}")
    Call<ApiResponse<AddressResponse>> setDefaultAddress(
            @Header("Authorization") String authHeader,
            @Path("id") String id
    );

    @DELETE("address/{id}")
    Call<ApiResponse<String>> deleteAddress(
            @Header("Authorization") String authHeader,
            @Path("id") String id
    );

}
