package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.DeletePromotionRequest;
import com.project.stationery_be_server.entity.Promotion;
import com.project.stationery_be_server.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    BigDecimal applyPromotion(String promoCode, BigDecimal orderTotal, User user);
    List<Promotion> getAvailablePromotions(User user, BigDecimal orderTotal);
    BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderTotal);
    void deletePromotion(DeletePromotionRequest request);
}
