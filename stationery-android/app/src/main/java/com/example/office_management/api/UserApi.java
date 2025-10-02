package com.example.office_management.api;

import com.example.office_management.dto.request.ChangePasswordRequest;
import com.example.office_management.dto.request.DeviceTokenRequest;
import com.example.office_management.dto.request.UpdateUserRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {
    @GET("users/info")
    Call<ApiResponse<UserResponse>> getUserInfo(@Header("Authorization") String authHeader);
    // Phương thức để cập nhật thông tin người dùng VÀ ảnh đại diện (Multipart)
    @Multipart
    @PUT("users/update-user") // Thay bằng endpoint đúng của bạn
    Call<ApiResponse<UserResponse>> updateUser(
            @Header("Authorization") String authHeader,
            @Part("document") RequestBody document, // Đây là phần JSON chứa thông tin người dùng
            @Part MultipartBody.Part file        // Đây là phần file ảnh
    );

    @POST("users/change-password")
    Call<ApiResponse<String>> changePassword(@Body ChangePasswordRequest request);

    @Multipart
    @POST("users/uploadImagesRv")
    Call<ApiResponse<String>> uploadImage(@Part MultipartBody.Part file);

    @PUT("users/{userId}/device-token")
    Call<Void> updateDeviceToken(@Header("Authorization") String authHeader, @Path("userId") String userId, @Body DeviceTokenRequest request);


}
