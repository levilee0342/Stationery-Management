package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.DetectionResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DetectionApi {
    @Multipart
    @POST("/predict")
    Call<DetectionResponse> detectProduct(@Part MultipartBody.Part image);
}
