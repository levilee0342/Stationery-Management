package com.project.stationery_be_server.utils;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.time.Instant;

@Component
public class OtpUtils {
    private final Random random;

    public OtpUtils() {
        this.random = new Random();
    }

    public int generateOTP() {
        return 100000 + random.nextInt(900000); // Tạo OTP 6 chữ số
    }

}
