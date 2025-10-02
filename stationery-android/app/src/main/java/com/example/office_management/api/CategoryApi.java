package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.category.CategoryProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CategoryApi {
    @GET("categories")
    Call<ApiResponse<List<CategoryProductResponse>>> apiAllCategory();
    @GET("search/top-user-categories")
    Call<ApiResponse<List<CategoryProductResponse>>> apiTopUserCategories(@Header("Authorization") String authHeader);
    @GET("search/hot-categories")
    Call<ApiResponse<List<CategoryProductResponse>>> apiTopHotCategories();
}
