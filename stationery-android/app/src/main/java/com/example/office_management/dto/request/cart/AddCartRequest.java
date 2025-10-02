package com.example.office_management.dto.request.cart;

public class AddCartRequest {
    private String productDetailId;
    private int quantity;

    public AddCartRequest(String productDetailId, int quantity){
        this.productDetailId = productDetailId;
        this.quantity = quantity;
    }

    public String getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(String productDetailId) {
        this.productDetailId = productDetailId;
    }

    public int getQuanlity() {
        return quantity;
    }

    public void setQuanlity(int quanlity) {
        this.quantity = quanlity;
    }
}
