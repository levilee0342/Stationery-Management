package com.example.office_management.api;

import com.example.office_management.dto.request.ChatRequest;
import com.example.office_management.dto.response.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ChatApi {
    @POST("/handle_message")
    Call<ChatResponse> sendMessage(@Body ChatRequest chatRequest);
}
