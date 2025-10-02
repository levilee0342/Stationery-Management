package com.example.office_management.dto.request;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    // @SerializedName đảm bảo khi JSON có key "message" sẽ map vào trường này
    @SerializedName("message")
    private String message;

    public ChatRequest(String message) {
        this.message = message;
    }

    // getter + setter (nếu cần)
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
