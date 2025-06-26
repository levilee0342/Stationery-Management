package com.project.stationery_be_server.Error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements BaseErrorCode {
    UNAUTHENTICATED(1002, "Username or password are incorrect", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "You do not have permission", HttpStatus.FORBIDDEN),
    MISSING_TOKEN(1004, "Missing token or token invalid", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1005, "Invalid token", HttpStatus.UNAUTHORIZED),
    BLOCKED(1006,"Your account has been blocked", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FAILD(1007,"Error while sending mail", HttpStatus.BAD_REQUEST),
    REGISTER_USER_FAILD(1008,"Failed to register user", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(1008,"OTP expired", HttpStatus.BAD_REQUEST),
    DELETE_CATEGORY_FAIL(1008,"Cannot delete category with associated products", HttpStatus.BAD_REQUEST),
    INCORRECT_OLD_PASSWORD(1008,"Incorrect old password", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME_AS_OLD(1008,"New password cannot be the same as old password", HttpStatus.BAD_REQUEST);
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
