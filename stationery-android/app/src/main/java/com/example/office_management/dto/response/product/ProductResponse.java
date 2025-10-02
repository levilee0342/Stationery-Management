package com.example.office_management.dto.response.product;

import com.example.office_management.dto.response.category.CategoryProductResponse;
import com.example.office_management.dto.response.colorSize.ColorSlugResponse;

import java.io.Serializable;
import java.util.List;

public class ProductResponse implements Serializable {
    private String productId;
    private String name;
    private String description;
    private CategoryProductResponse category;
    private String slug;
    private int minPrice;
    private double totalRating;
    private int soldQuantity;
    private int quantity;
    private String img;
    private ProductDetailResponse productDetail;
    private List<ColorSlugResponse> fetchColor;
    private String createdAt;

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CategoryProductResponse getCategory() {
        return category;
    }

    public String getSlug() {
        return slug;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImg() {
        return img;
    }

    public ProductDetailResponse getProductDetail() {
        return productDetail;
    }

    public List<ColorSlugResponse> getFetchColor() {
        return fetchColor;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

