package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.*;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.UserInfoResponse;
import com.project.stationery_be_server.dto.response.UserResponse;
import com.project.stationery_be_server.dto.response.product.ProductResponse;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.UploadImageFile;
import com.project.stationery_be_server.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    UploadImageFile uploadImageFile;
    UserRepository userRepository;

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAll())
                .build();
    }

    @PostMapping("/upload") // Sửa @RequestMapping thành @PostMapping cho ngắn gọn
    public ApiResponse<Map> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.<Map>builder()
                .result(uploadImageFile.uploadImageFile(file))
                .build();
    }

    @GetMapping("/info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserInfo())
                .build();
    }

    @GetMapping("/info/{userId}")
    public ApiResponse<UserResponse> getUerById(@PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<String> registerUser(@RequestBody RegisterRequest request) {
        String message = userService.register(request);
        return ApiResponse.<String>builder()
                .message("Registration initiated, please check your email for OTP")
                .result(message)
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<UserResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        UserResponse userResponse = userService.verifyOtp(request);
        return ApiResponse.<UserResponse>builder()
                .message("User registered successfully")
                .result(userResponse)
                .build();
    }

    @PostMapping("/resend-otp")
    public ApiResponse<String> resendOtp(@RequestBody OtpVerificationRequest request) {
        String message = userService.resendOtp(request.getEmail());
        return ApiResponse.<String>builder()
                .message("OTP sent successfully")
                .result(message)
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        String message = userService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
        return ApiResponse.<String>builder()
                .message("Password changed successfully")
                .result(message)
                .build();
    }

    @PutMapping("/update-user")
    public ApiResponse<UserResponse> updateUser(@RequestPart("document") String documentJson, @RequestPart(value = "file", required = false) MultipartFile file) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(documentJson, file))
                .build();
    }


    @DeleteMapping("/delete-user")
    public ApiResponse<String> deleteUser(@RequestBody DeleteUserRequest request) {
        userService.deleteUser(request);
        return ApiResponse.<String>builder()
                .result("User deleted successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/admin/update-user/{id}")
    public ApiResponse<UserResponse> updateUserAdmin(
            @PathVariable("id") String userId,
            @RequestPart("document") String documentJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserAdmin(documentJson, userId, file))
                .build();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/get-users")
    public ApiResponse<Page<UserInfoResponse>> getAllProductsForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int limit,
                                                                      @RequestParam(required = false) String search,
                                                                      @RequestParam(required = false) String roleId

    ) {
        // sử lý ở FE page 1 là BE page 0, page 2 là page 1, ..
        page = page <= 1 ? 0 : page - 1;
        Pageable pageable;

        pageable = PageRequest.of(page, limit);

        UserFilterRequest filterRequest = UserFilterRequest.builder()
                .search(search)
                .roleId(roleId)
                .build();
        Page<UserInfoResponse> pageResult = userService.getAllUsers(pageable, filterRequest);
        return ApiResponse.<Page<UserInfoResponse>>builder()
                .result(pageResult)
                .build();
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/admin/block-user/{id}")
    public ApiResponse<UserResponse> blockUser(
            @PathVariable("id") String userId
    ) {
        userService.blockUser(userId);
        return ApiResponse.<UserResponse>builder()
                .message("User blocked successfully")
                .build();
    }

    @PutMapping("/{userId}/device-token")
    public ResponseEntity<ApiResponse<String>> updateDeviceToken(@PathVariable String userId, @RequestBody DeviceTokenRequest request) {
        // Kiểm tra xác thực
        String authenticatedUserId = getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.<String>builder()
                            .code(403)
                            .message("You are not authorized to update this user's device token")
                            .build());
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.<String>builder()
                            .code(404)
                            .message("User not found")
                            .build());
        }

        // Kiểm tra token hợp lệ
        String deviceToken = request.getDeviceToken();
        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.<String>builder()
                            .code(400)
                            .message("Device token cannot be null or empty")
                            .build());
        }

        // Kiểm tra định dạng token cơ bản (FCM token thường dài hơn 100 ký tự)
        if (deviceToken.length() < 100) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.<String>builder()
                            .code(400)
                            .message("Invalid device token format")
                            .build());
        }

        User user = userOptional.get();
        user.setDeviceToken(request.getDeviceToken());
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .result("Device token updated successfully")
                .build());
    }

    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        return authentication.getName();
    }
}