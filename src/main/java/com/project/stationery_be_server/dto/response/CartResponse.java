package com.project.stationery_be_server.dto.response;

import com.project.stationery_be_server.entity.ProductPromotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    String userId;
    String productId;
    String productDetailId;
    String productName;
    String colorName;
    String sizeName;
    List<ProductPromotion> productPromotion;
    int quantity;
    int originalPrice;
    int discountPrice;
    int discountValue;
    Date createdAt;
    String imageUrl;
}
