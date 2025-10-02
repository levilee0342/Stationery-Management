package com.example.office_management.dto.request.order;

public class CancelOrderRequest {
    private String cancelReason;

    public CancelOrderRequest() {
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
