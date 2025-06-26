package com.project.stationery_be_server.dto.request;

import com.project.stationery_be_server.entity.Promotion;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePromotionRequest {
    private String promotionId;
    String promoCode;
    Promotion.DiscountType discountType;
    Integer discountValue;
    Integer maxValue;
    Integer usageLimit;
    Integer minOrderValue;
    LocalDateTime startDate;
    LocalDateTime endDate;

}