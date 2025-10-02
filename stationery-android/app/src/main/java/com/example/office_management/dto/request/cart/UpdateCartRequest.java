package com.example.office_management.dto.request.cart;

public class UpdateCartRequest {
    private int quantity;

    public UpdateCartRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
