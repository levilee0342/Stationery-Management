package com.project.stationery_be_server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.stationery_be_server.Error.AuthErrorCode;
import com.project.stationery_be_server.Error.InvalidErrorCode;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.*;
import com.project.stationery_be_server.dto.response.UserResponse;
import com.project.stationery_be_server.entity.Role;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.mapper.UserMapper;
import com.project.stationery_be_server.repository.RoleRepository;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.EmailService;
import com.project.stationery_be_server.service.UserService;
import com.project.stationery_be_server.utils.OtpUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserMapper userMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    EmailService emailService;
    OtpUtils otpUtils;
    RoleRepository roleRepository;
    Cloudinary cloudinary;

    // Temporary storage for pending registrations
    private final Map<String, RegisterRequest> pendingRegistrations = new HashMap<>();
    private final Map<String, OtpDetails> otpStorage = new HashMap<>();


    private static class OtpDetails {
        Integer otp;
        Date createdAt;

        OtpDetails(Integer otp, Date createdAt) {
            this.otp = otp;
            this.createdAt = createdAt;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse createUserFromGoogle(String email, String fullName, String avatar) {
        Role role = roleRepository.findById("112")
                .orElseThrow(() -> new RuntimeException("Role User not found"));

        // Handle fullName robustly
        String firstName = fullName != null && !fullName.isEmpty() ? fullName : "Unknown";
        String lastName = "";

        if (fullName != null && fullName.contains(" ")) {
            String[] nameParts = fullName.trim().split("\\s+", 2);
            firstName = nameParts[0];
            lastName = nameParts[1];
        }

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setBlocked(false);
        user.setAvatar(avatar != null ? avatar : "");

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new AppException(NotExistedErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (pendingRegistrations.containsKey(email)) {
            throw new AppException(NotExistedErrorCode.PENDING_REGISTRATION_EXISTS);
        }
        return sendOtp(request);
    }

    @Override
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        // Check if user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
                });
        // Generate and store OTP
        Integer otp = otpUtils.generateOTP();
        otpStorage.put(email, new OtpDetails(otp, new Date()));
        try {
            emailService.sendSimpleMail(new EmailRequest(email, otp));
            return "OTP sent to " + email;
        } catch (Exception e) {
            otpStorage.remove(email);
            throw new AppException(AuthErrorCode.SEND_MAIL_FAILD);
        }
    }

    @Override
    @Transactional
    public UserResponse resetPassword(OtpVerificationRequest otpRequest, String newPassword) {
        String email = otpRequest.getEmail();
        Integer userOtp = otpRequest.getOtp();
        // Verify user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
                });
        // Verify OTP
        OtpDetails otpDetails = otpStorage.get(email);
        if (otpDetails == null) {
            throw new AppException(NotExistedErrorCode.OTP_NOT_FOUND);
        }
        // Check OTP expiration (5 minutes)
        if (otpDetails.createdAt == null ||
                (new Date().getTime() - otpDetails.createdAt.getTime() > 300_000)) {
            otpStorage.remove(email);
            throw new AppException(AuthErrorCode.OTP_EXPIRED);
        }
        // Verify OTP match
        if (!otpDetails.otp.equals(userOtp)) {
            throw new AppException(InvalidErrorCode.INVALID_OTP);
        }
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(otpDetails.otp);
        user.setOtpCreatedAt(otpDetails.createdAt);
        User updatedUser = userRepository.save(user);
        // Clean up OTP storage
        otpStorage.remove(email);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    public UserResponse getUserInfo() {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse verifyOtp(OtpVerificationRequest otpRequest) {
        String email = otpRequest.getEmail();
        Integer userOtp = otpRequest.getOtp();
        OtpDetails otpDetails = otpStorage.get(email);
        if (otpDetails == null) {
            throw new AppException(NotExistedErrorCode.OTP_NOT_FOUND);
        }
        if (otpDetails.createdAt == null ||
                (new Date().getTime() - otpDetails.createdAt.getTime() > 300_000)) {
            otpStorage.remove(email);
            pendingRegistrations.remove(email);
            throw new AppException(AuthErrorCode.OTP_EXPIRED);
        }
        if (!otpDetails.otp.equals(userOtp)) {
            throw new AppException(InvalidErrorCode.INVALID_OTP);
        }
        RegisterRequest request = pendingRegistrations.get(email);
        if (request == null) {
            throw new AppException(NotExistedErrorCode.PENDING_REGISTRATION_NOT_FOUND);
        }
        Role role = roleRepository.findById("112")
                .orElseThrow(() -> new RuntimeException("Role User not found"));
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .isBlocked(false)
                .otp(otpDetails.otp)  // Store OTP in DB
                .otpCreatedAt(otpDetails.createdAt)
                .role(role)
                .build();
        User savedUser = userRepository.save(user);
        pendingRegistrations.remove(email);
        otpStorage.remove(email);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public String resendOtp(String email) {
        RegisterRequest request = pendingRegistrations.get(email);
        if (request == null) {
            throw new AppException(NotExistedErrorCode.PENDING_REGISTRATION_NOT_FOUND);
        }
        return sendOtp(request);
    }

    private String sendOtp(RegisterRequest request) {
        String email = request.getEmail();
        Integer otp = otpUtils.generateOTP();
        // Store registration details and OTP in memory
        pendingRegistrations.put(email, request);
        otpStorage.put(email, new OtpDetails(otp, new Date()));
        try {
            emailService.sendSimpleMail(new EmailRequest(email, otp));
            return "OTP sent to " + email;
        } catch (Exception e) {
            pendingRegistrations.remove(email);
            otpStorage.remove(email);
            throw new AppException(AuthErrorCode.SEND_MAIL_FAILD);
        }
    }

    @Override
    @Transactional
    public String changePassword(String email, String oldPassword, String newPassword) {
        //Check user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new AppException(NotExistedErrorCode.USER_NOT_EXISTED);
                });
        // Check password old
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(AuthErrorCode.INCORRECT_OLD_PASSWORD);
        }
        // Check new password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(AuthErrorCode.PASSWORD_SAME_AS_OLD);
        }
        // Update new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Override
    public UserResponse updateUser(String documentJson, MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserRequest request = null;
        try {
            request = objectMapper.readValue(documentJson, UserRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        try {
            if (file != null && !file.isEmpty()) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String url = uploadResult.get("secure_url").toString(); // hoặc "url"
                user.setAvatar(url);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Cloudinary");
        }
        user.setFirstName(request.getFirstName() != null ? request.getFirstName() : user.getFirstName());
        user.setLastName(request.getLastName() != null ? request.getLastName() : user.getLastName());
        user.setEmail(request.getEmail() != null ? request.getEmail() : user.getEmail());
        user.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhone());
        user.setDob(request.getDob() != null ? request.getDob() : user.getDob());
//        user.setAvatar(request.getAvatar() != null ? request.getAvatar() : user.getAvatar());

        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(DeleteUserRequest request){
        //lay user hien tai dang dung
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // admin moi dc xoa
        if (!user.getRole().getRoleName().equals("admin")){
            throw new RuntimeException("You do not have permission to delete users");
        }
        // kiem tra user bi xoa
        String userIdDeleted = request.getUserId();
        User userDeleted = userRepository.findById(userIdDeleted)
                .orElseThrow(()->new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // k xoa ban than
        if (userIdDeleted.equals(userIdLogin)) {
            throw new RuntimeException("You can not delete yourself.");
        }
        // cac table co du lieu lien qua
        if (userRepository.hasReview(userIdDeleted)
                || userRepository.hasAddress(userIdDeleted)
                || userRepository.hasCart(userIdDeleted)
                || userRepository.hasPurchaseOrder(userIdDeleted)
                || userRepository.hasUserPromotion(userIdDeleted)) {

            throw new RuntimeException("Can not delete this user because of having data.");
        }
        userRepository.delete(userDeleted);
    }
}
