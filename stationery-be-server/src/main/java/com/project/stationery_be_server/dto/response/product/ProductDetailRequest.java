package com.project.stationery_be_server.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailRequest {
    private String slug;
    private String name;
    private int originalPrice;
    private int stockQuantity;
    private int discountPrice;
    private String sizeId;
    private String colorId;
}
