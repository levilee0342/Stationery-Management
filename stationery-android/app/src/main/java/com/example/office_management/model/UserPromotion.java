package com.example.office_management.model;

import com.example.office_management.dto.response.UserResponse;

import java.io.Serializable;
import java.util.Set;

public class UserPromotion implements Serializable {
    private String userPromotionId;
    private Promotion promotion;
    private UserResponse user;
    //private Set<PurchaseOrder> purchaseOrders;

    public UserPromotion() {}

    public String getUserPromotionId() {
        return userPromotionId;
    }

    public void setUserPromotionId(String userPromotionId) {
        this.userPromotionId = userPromotionId;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

/*    public Set<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(Set<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }*/
}
