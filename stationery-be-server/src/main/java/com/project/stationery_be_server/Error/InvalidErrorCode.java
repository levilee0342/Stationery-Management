package com.project.stationery_be_server.Error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InvalidErrorCode implements BaseErrorCode {
    USERNAME_INVALID(2001, "Username must be at least 8 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(2002, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    INVALID_KEY(2004, "Invalid key", HttpStatus.BAD_REQUEST),
    INVALID_DOB(2005, "Date of birth must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_OTP(2006, "Invalid OTP", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    InvalidErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
