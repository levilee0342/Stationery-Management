package com.example.office_management.model;

import java.io.Serializable;

public class ProductPromotion implements Serializable {
    private String productPromotionId;
    private Promotion promotion;

    public ProductPromotion() {}

    public String getProductPromotionId() {
        return productPromotionId;
    }

    public void setProductPromotionId(String productPromotionId) {
        this.productPromotionId = productPromotionId;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}
