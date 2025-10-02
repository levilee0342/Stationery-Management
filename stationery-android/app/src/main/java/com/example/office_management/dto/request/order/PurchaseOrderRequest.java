package com.example.office_management.dto.request.order;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrderRequest implements Serializable {
    private List<PurchaseOrderProductRequest> orderDetails;
    private String userPromotionId;
    private String recipient;
    private String addressId;
    private String note;
    private LocalDateTime expiredTime;

    public PurchaseOrderRequest(List<PurchaseOrderProductRequest> orderDetails, String userPromotionId, String recipient, String addressId, String note, LocalDateTime expiredTime){
        this.orderDetails = orderDetails;
        this.userPromotionId = userPromotionId;
        this.recipient = recipient;
        this.addressId = addressId;
        this.note = note;
        this.expiredTime = expiredTime;
    }

    public List<PurchaseOrderProductRequest> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<PurchaseOrderProductRequest> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getUserPromotionId() {
        return userPromotionId;
    }

    public void setUserPromotionId(String userPromotionId) {
        this.userPromotionId = userPromotionId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }
}
