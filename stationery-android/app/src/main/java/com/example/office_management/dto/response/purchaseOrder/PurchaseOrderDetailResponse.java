package com.example.office_management.dto.response.purchaseOrder;

public class PurchaseOrderDetailResponse {
    private String productDetailId;
    private int quantity;

    public PurchaseOrderDetailResponse() {
    }

    public String getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(String productDetailId) {
        this.productDetailId = productDetailId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
