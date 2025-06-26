package com.project.stationery_be_server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    String accessToken;
    UserResponse userData;
}
