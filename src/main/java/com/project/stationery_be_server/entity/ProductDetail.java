package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.*;
import com.project.stationery_be_server.dto.response.ColorSizeSlugResponse;
import com.project.stationery_be_server.dto.response.ColorSlugResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_detail_id")
    private String productDetailId;

    @Column(length = 100,unique = true, nullable = false)
    private String slug;

    @Column(name="name",unique = true, nullable = false)
    private String name;

    private String thumbnail;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "sold_quantity")
    private int soldQuantity;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @Column(name = "original_price")
    private int originalPrice;

    @Column(name = "discount_price")
    private int discountPrice;
    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;
    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProductPromotion> productPromotions;

    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<PurchaseOrderDetail> purchaseOrderDetails;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    private List<Image> images;

}
