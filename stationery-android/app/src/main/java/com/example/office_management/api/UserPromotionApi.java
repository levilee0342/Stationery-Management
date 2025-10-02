package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.model.UserPromotion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserPromotionApi {
    @GET("user-promotions")
    Call<ApiResponse<List<UserPromotion>>> getUserPromotions(@Header("Authorization") String authHeader);
    
}
