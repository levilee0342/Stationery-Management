package com.project.stationery_be_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductDetailRequest {
    private String productDetailId;
    private int originalPrice;
    private int discountPrice;
    private String slug;
    private String name;
    private int stockQuantity;
    private String sizeId;
    private String colorId;
    private List<String> deleteImages;
}