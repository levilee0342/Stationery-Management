package com.example.office_management.dto.request.order;

import java.io.Serializable;

public class PurchaseOrderProductRequest implements Serializable {
    private String productDetailId;
    private Integer quantity;
    private String productPromotionId;

    // Thêm constructor
    public PurchaseOrderProductRequest(String productDetailId, Integer quantity, String productPromotionId) {
        this.productDetailId = productDetailId;
        this.quantity = quantity;
        this.productPromotionId = productPromotionId;
    }

    public String getProductDetailId() {
        return productDetailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getProductPromotionId() {
        return productPromotionId;
    }
}
