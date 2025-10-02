package com.example.office_management.api;

import com.example.office_management.dto.request.OtpVerificationRequest;
import com.example.office_management.dto.request.RegisterRequest;
import com.example.office_management.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterApi {
    @POST("users/register")
    Call<User> register(@Body RegisterRequest RegisterRequest);

    @POST("users/verify-otp")
    Call<Void> verifyOTP(@Body OtpVerificationRequest otpVerificationRequest);

    @POST("users/resend-otp")
    Call<Void> resendOtp(@Body OtpVerificationRequest otpVerificationRequest);
}
