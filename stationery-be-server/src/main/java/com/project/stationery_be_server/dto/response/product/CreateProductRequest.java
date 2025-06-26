package com.project.stationery_be_server.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
    private String name;
    private String description;
    private String slug;
    private String categoryId;
    private List<ProductDetailRequest> productDetails;
}
