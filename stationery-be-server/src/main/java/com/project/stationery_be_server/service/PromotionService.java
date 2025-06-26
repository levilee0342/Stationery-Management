package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.DeletePromotionRequest;
import com.project.stationery_be_server.dto.request.PromotionRequest;
import com.project.stationery_be_server.dto.request.UpdatePromotionRequest;
import com.project.stationery_be_server.dto.response.promotion.PromotionResponse;
import com.project.stationery_be_server.entity.ProductPromotion;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.entity.ProductPromotion;
import com.project.stationery_be_server.entity.Promotion;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.entity.UserPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    BigDecimal applyPromotion(String promoCode, BigDecimal orderTotal, User user);
    List<Promotion> getAvailablePromotions(User user, BigDecimal orderTotal);
    BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderTotal);
    void deletePromotion(DeletePromotionRequest request);
    void createPromotion(PromotionRequest request);
    void updatePromotion(UpdatePromotionRequest request);
    Page<Promotion> getMyVouchers(Pageable pageable);
    Page<UserPromotion> getAllUserVouchers(Pageable pageable);
    Page<PromotionResponse> getAllPromotion(Pageable pageable, String search);
    Promotion getPromotionById(String promotionId);
    Page<Promotion> getPromotionsByUser(String userId, Pageable pageable);
    Page<Promotion> getPromotionsByProduct(String productId, Pageable pageable);
    Page<ProductPromotion> getAllProductPromotionPagination(Pageable pageable, String search);

}
