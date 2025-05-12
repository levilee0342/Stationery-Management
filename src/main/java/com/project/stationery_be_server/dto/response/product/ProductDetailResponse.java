package com.project.stationery_be_server.dto.response.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.stationery_be_server.entity.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    private String productDetailId;
    private String slug;
    private String name;
    private int stockQuantity;
    private int soldQuantity;
    private int originalPrice;
    private int discountPrice;
    private Size size;
    private Color color;
    private LocalDateTime createdAt;
    private List<Image> images;
}
