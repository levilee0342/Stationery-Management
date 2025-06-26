package com.project.stationery_be_server.dto.request;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String email;
    private int otp;
}
