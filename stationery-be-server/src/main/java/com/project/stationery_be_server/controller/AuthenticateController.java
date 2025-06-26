package com.project.stationery_be_server.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.project.stationery_be_server.dto.request.ForgotPasswordRequest;
import com.project.stationery_be_server.dto.request.LoginRequest;
import com.project.stationery_be_server.dto.request.OtpVerificationRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.LoginGoogleResponse;
import com.project.stationery_be_server.dto.response.LoginResponse;
import com.project.stationery_be_server.dto.response.UserResponse;
import com.project.stationery_be_server.entity.InvalidatedToken;
import com.project.stationery_be_server.mapper.UserMapper;
import com.project.stationery_be_server.repository.InvalidatedTokenRepository;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.AuthenticateService;
import com.project.stationery_be_server.service.UserService;
import com.project.stationery_be_server.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticateController {
    private final AuthenticateService authenticateService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final String googleClientId;

    public AuthenticateController(
            AuthenticateService authenticateService,
            UserRepository userRepository,
            UserService userService,
            InvalidatedTokenRepository invalidatedTokenRepository,
            UserMapper userMapper,
            JwtUtils jwtUtils,
            @Value("${google.client.id}") String googleClientId) {
        this.authenticateService = authenticateService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.googleClientId = googleClientId;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .message("Login successfully")
                .result(authenticateService.login(request))
                .build();
    }

    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse<LoginGoogleResponse>> loginWithGoogle(@RequestBody Map<String, String> request) {
        String tokenId = request.get("token");
        if (tokenId == null || tokenId.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<LoginGoogleResponse>builder()
                            .message("Google token is required")
                            .build()
            );
        }

        try {
            // Xác thực id_token với Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(tokenId);
            if (idToken == null) {
                return ResponseEntity.status(401).body(
                        ApiResponse.<LoginGoogleResponse>builder()
                                .message("Invalid Google token")
                                .build()
                );
            }

            // Lấy thông tin từ payload
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String avatar = (String) payload.get("picture");

            // Kiểm tra hoặc tạo user
            var userOptional = userRepository.findByEmail(email);
            boolean isNewUser = userOptional.isEmpty();
            UserResponse userResponse = userOptional.map(userMapper::toUserResponse)
                    .orElseGet(() -> userService.createUserFromGoogle(email, name != null ? name : "Unknown", avatar != null ? avatar : ""));

            // Tạo JWT token
            String token = jwtUtils.generateToken(userResponse.getUserId());
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("Failed to generate JWT token");
            }

            // Build response
            LoginGoogleResponse loginGoogleResponse = LoginGoogleResponse.builder()
                    .accessToken(token)
                    .userData(userResponse)
                    .isNewUser(isNewUser)
                    .build();

            return ResponseEntity.ok(
                    ApiResponse.<LoginGoogleResponse>builder()
                            .message("Login with Google successfully")
                            .result(loginGoogleResponse)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ApiResponse.<LoginGoogleResponse>builder()
                            .message("Error processing Google login: " + e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/login/google/oauth2")
    public ResponseEntity<ApiResponse<LoginGoogleResponse>> loginWithGoogleOAuth2(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new IllegalStateException("Email not found in OAuth2 user attributes");
        }

        String name = oAuth2User.getAttribute("name") != null ? oAuth2User.getAttribute("name") : "Unknown";
        String avatar = oAuth2User.getAttribute("picture") != null ? oAuth2User.getAttribute("picture") : "";

        // Kiểm tra hoặc tạo user
        var userOptional = userRepository.findByEmail(email);
        boolean isNewUser = userOptional.isEmpty();
        UserResponse userResponse = userOptional.map(userMapper::toUserResponse)
                .orElseGet(() -> userService.createUserFromGoogle(email, name, avatar));

        // Tạo JWT token
        String token = jwtUtils.generateToken(userResponse.getUserId());
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Failed to generate JWT token");
        }

        // Build response
        LoginGoogleResponse loginGoogleResponse = LoginGoogleResponse.builder()
                .accessToken(token)
                .userData(userResponse)
                .isNewUser(isNewUser)
                .build();

        return ResponseEntity.ok(
                ApiResponse.<LoginGoogleResponse>builder()
                        .message("Login with Google successfully")
                        .result(loginGoogleResponse)
                        .build()
        );
    }

    @GetMapping("/failure")
    public ResponseEntity<ApiResponse<String>> handleAuthFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<String>builder()
                        .message("OAuth 2.0 login failed")
                        .result("Please try again")
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<String>builder()
                            .message("Authorization header is missing")
                            .build()
            );
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (invalidatedTokenRepository.existsById(token)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<String>builder()
                            .message("Token already blacklisted")
                            .build()
            );
        }
        InvalidatedToken invalidatedToken = new InvalidatedToken(token, LocalDateTime.now());
        invalidatedTokenRepository.save(invalidatedToken);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Logout successful")
                        .result("Token invalidated successfully")
                        .build()
        );
    }

    @GetMapping
    public ApiResponse<?> getUserInfo() {
        return ApiResponse.builder().result(
                userRepository.findByEmail("letuannhat105@gmail.com").get().getRole()
        ).build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String message = userService.forgotPassword(request);
        return ApiResponse.<String>builder()
                .message("Password reset initiated, please check your email for OTP")
                .result(message)
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<UserResponse> resetPassword(
            @RequestBody OtpVerificationRequest otpRequest,
            @RequestParam String newPassword) {
        UserResponse userResponse = userService.resetPassword(otpRequest, newPassword);
        return ApiResponse.<UserResponse>builder()
                .message("Password reset successfully")
                .result(userResponse)
                .build();
    }
}