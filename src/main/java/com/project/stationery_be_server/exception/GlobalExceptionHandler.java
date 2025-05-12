package com.project.stationery_be_server.exception;

import com.project.stationery_be_server.Error.AuthErrorCode;
import com.project.stationery_be_server.Error.BaseErrorCode;
import jakarta.validation.ConstraintViolation;
import com.project.stationery_be_server.Error.InvalidErrorCode;
import com.project.stationery_be_server.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> exceptionHandler(Exception ex) {
        ex.printStackTrace();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .code(400)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConcurrentModificationException.class)
    ResponseEntity<ApiResponse<Void>> exceptionHandler(ConcurrentModificationException ex) {
        ex.printStackTrace();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(ex.getMessage())
                .code(401)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    //    Báo lỗi k tìm thấy đường dẫn
    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> noResourceFoundException(NoResourceFoundException e) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Path does not exist");
        apiResponse.setCode(404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> appExceptionHandler(AppException e) {
        System.out.println(e.getMessage());
        BaseErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    //    Lỗi validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        InvalidErrorCode errorCode = InvalidErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = InvalidErrorCode.valueOf(enumKey);
            var constraintViolations =
                    e.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = constraintViolations.getConstraintDescriptor().getAttributes();
        } catch (IllegalArgumentException ex) {
        }
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = attributes.get(MIN_ATTRIBUTE).toString();
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
