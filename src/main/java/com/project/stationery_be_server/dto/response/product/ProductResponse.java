package com.project.stationery_be_server.dto.response.product;

import com.project.stationery_be_server.dto.response.CategoryProductResponse;
import com.project.stationery_be_server.dto.response.ColorSlugResponse;
import com.project.stationery_be_server.entity.ProductDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String productId;
    String name;
    String description;
    CategoryProductResponse category;
    String slug;
    Integer minPrice;
    Double totalRating;
    Integer soldQuantity;
    Integer quantity;
    List<ColorSlugResponse> fetchColor;
    LocalDateTime createdAt;
    String img;

    ProductDetail productDetail;
}
