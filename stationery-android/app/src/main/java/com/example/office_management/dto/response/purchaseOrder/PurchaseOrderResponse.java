package com.example.office_management.dto.response.purchaseOrder;

import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.product.ProductDetailResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class PurchaseOrderResponse {
    private String purchaseOrderId;
    private Date createdAt;
    private String pdfUrl;
    private String productPromotionId;
    private String userPromotionId;
    private Status status;
    private BigDecimal amount;
    private String note;
    private String cancelReason;
    private LocalDateTime expiredTime;
    private AddressResponse address;
    private List<ProductDetailResponse> productDetails;
    private List<PurchaseOrderDetailResponse> orderDetails;

    public enum Status {
        PENDING,        // Chờ xác nhận
        PROCESSING,     // Đang xử lý
        SHIPPING,       // Đang giao
        COMPLETED,      // Hoàn thành
        CANCELED        // Đã hủy
    }

    public PurchaseOrderResponse() {
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getProductPromotionId() {
        return productPromotionId;
    }

    public void setProductPromotionId(String productPromotionId) {
        this.productPromotionId = productPromotionId;
    }

    public String getUserPromotionId() {
        return userPromotionId;
    }

    public void setUserPromotionId(String userPromotionId) {
        this.userPromotionId = userPromotionId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public List<PurchaseOrderDetailResponse> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<PurchaseOrderDetailResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public AddressResponse getAddress() {
        return address;
    }

    public void setAddress(AddressResponse address) {
        this.address = address;
    }

    public List<ProductDetailResponse> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductDetailResponse> productDetails) {
        this.productDetails = productDetails;
    }
}
