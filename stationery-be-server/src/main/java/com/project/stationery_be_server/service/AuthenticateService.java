package com.project.stationery_be_server.service;


import com.project.stationery_be_server.dto.request.LoginRequest;
import com.project.stationery_be_server.dto.response.LoginResponse;

public interface AuthenticateService {
    LoginResponse login (LoginRequest loginRequest);
}
