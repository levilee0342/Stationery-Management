package com.project.stationery_be_server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.stationery_be_server.entity.Promotion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionRequest {
    String promoCode;
    VoucherType voucherType;
    Promotion.DiscountType discountType;
    Integer discountValue;
    Integer maxValue;
    Integer usageLimit;
    Integer minOrderValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    public enum VoucherType {
        ALL_PRODUCTS, ALL_USERS, PRODUCTS, USERS
    }
    List<String> userIds;
    List<String> productIds;
}

