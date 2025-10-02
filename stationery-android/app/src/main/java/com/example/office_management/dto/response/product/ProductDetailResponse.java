package com.example.office_management.dto.response.product;

import com.example.office_management.dto.response.ImageResponse;
import com.example.office_management.dto.response.colorSize.SizeResponse;
import com.example.office_management.dto.response.colorSize.ColorResponse;

import java.io.Serializable;
import java.util.List;

public class ProductDetailResponse implements Serializable {
    private String productDetailId;
    private String slug;
    private String name;
    private String thumbnail;
    private int stockQuantity;
    private int soldQuantity;
    private int originalPrice;
    private int discountPrice;
    private SizeResponse size;
    private ColorResponse color;
    private List<ImageResponse> images;

    public String getProductDetailId() {
        return productDetailId;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public SizeResponse getSize() {
        return size;
    }

    public ColorResponse getColor() {
        return color;
    }

    public List<ImageResponse> getImages() {
        return images;
    }
}
