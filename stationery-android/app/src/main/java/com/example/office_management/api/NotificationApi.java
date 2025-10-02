package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.NotificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationApi {
    @GET("notifications")
    Call<ApiResponse<List<NotificationResponse>>> getUserNotifications(@Header("Authorization") String authHeader);

    @PUT("notifications/{id}/read")
    Call<ApiResponse<Void>> markAsRead(
            @Path("id") String id
    );

    @GET("notifications/unread-count")
    Call<ApiResponse<Long>> countUnreadNotifications(@Header("Authorization") String authHeader);
}
