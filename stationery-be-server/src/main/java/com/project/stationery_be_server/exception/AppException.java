package com.project.stationery_be_server.exception;

import com.project.stationery_be_server.Error.BaseErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    private BaseErrorCode errorCode;
    public AppException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
