package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.DetectionResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.colorSize.ColorSizeSlugResponse;
import com.example.office_management.dto.response.product.ProductResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products")
    Call<ApiResponse<ResultResponse>> apiGetAllProducts(
            @Query("sortBy") String sortBy,
            @Query("minPrice") String minPrice,
            @Query("maxPrice") String maxPrice,
            @Query("categoryId") String categoryId,
            @Query("search") String search,
            @Query("totalRating") String totalRating,
            @Query("page") int page,
            @Query("limit") int   limit
    );

    @GET("products/{slug}")
    Call<ApiResponse<ProductResponse>> getProductDetail(
            @Path("slug") String slug
    );

    @GET("products/color-size/{slug}")
    Call<ApiResponse<List<ColorSizeSlugResponse>>> getColorSizeSlug(
            @Path("slug") String slug
    );

}
