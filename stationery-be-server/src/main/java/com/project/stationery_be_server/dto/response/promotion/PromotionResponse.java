package com.project.stationery_be_server.dto.response.promotion;

import com.project.stationery_be_server.entity.Promotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionResponse {
    private String promotionId;
    private String promoCode;
    private Promotion.DiscountType discountType;
    private Integer discountValue;
    private Integer usageLimit;
    private Integer tempUsageLimit;
    private Integer maxValue;
    private Integer minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;

    List<ProductDetailPromotion> pd;
    List<UserInPromotion> user;

}
