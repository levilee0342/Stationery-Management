package com.example.office_management.api;

import com.example.office_management.dto.request.review.ReviewRequest;
import com.example.office_management.dto.request.review.UpdateReviewRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ReviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReviewApi {
    @GET ("reviews/{slug}")
    Call<ApiResponse<List<ReviewResponse>>> getReviewByProductId( @Path("slug") String slug );

    @POST("reviews")
    Call<ApiResponse<Void>> createReview(
            @Header("Authorization") String authHeader,
            @Body ReviewRequest request
    );

    @PUT("reviews/{id}")
    Call<ApiResponse<ApiResponse<Void>>> updateReview(
            @Header("Authorization") String authHeader,
            @Path("id") String id,
            @Body UpdateReviewRequest request
    );

    @DELETE("reviews/{id}")
    Call<ApiResponse<ApiResponse<Void>>> deleteReview(
            @Header("Authorization") String authHeader,
            @Path("id") String id
    );
}
