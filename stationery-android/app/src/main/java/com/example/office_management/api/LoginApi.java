package com.example.office_management.api;

import com.example.office_management.dto.request.ForgotPasswordRequest;
import com.example.office_management.dto.request.GoogleLoginRequest;
import com.example.office_management.dto.request.LoginRequest;
import com.example.office_management.dto.request.OtpVerificationRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.LoginResponse;
import com.example.office_management.dto.response.auth.LoginGoogleResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/login/google")
    Call<ApiResponse<LoginGoogleResponse>> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Header("Authorization") String token);

    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body OtpVerificationRequest otpVerificationRequest, @Query("newPassword") String newPassword);
}
