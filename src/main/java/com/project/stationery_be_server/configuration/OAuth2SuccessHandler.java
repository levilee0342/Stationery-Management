package com.project.stationery_be_server.configuration;

import com.project.stationery_be_server.dto.response.LoginResponse;
import com.project.stationery_be_server.mapper.UserMapper;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.UserService;
import com.project.stationery_be_server.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    public OAuth2SuccessHandler(UserRepository userRepository, UserService userService, UserMapper userMapper, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new IllegalStateException("Email not found in OAuth2 user attributes");
        }
        String name = oAuth2User.getAttribute("name") != null ? oAuth2User.getAttribute("name") : "Unknown";
        String avatar = oAuth2User.getAttribute("picture") != null ? oAuth2User.getAttribute("picture") : "";

        // Kiểm tra hoặc tạo user
        var userOptional = userRepository.findByEmail(email);
        var userResponse = userOptional.map(userMapper::toUserResponse)
                .orElseGet(() -> userService.createUserFromGoogle(email, name, avatar));

        // Tạo JWT token
        String token = jwtUtils.generateToken(userResponse.getUserId());
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Failed to generate JWT token");
        }

        // Tạo response
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(token)
                .userData(userResponse)
                .build();

        // Chuyển hướng về frontend với token
        response.sendRedirect("http://localhost:5173/auth?mode=login&token=" + token);
    }
}