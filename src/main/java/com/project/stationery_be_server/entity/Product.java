package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.*;
import com.project.stationery_be_server.dto.response.ColorSlugResponse;
import com.project.stationery_be_server.listener.ProductEntityListener;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(ProductEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private String productId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "slug", length = 100, nullable = false)
    private String slug;
    @Column(name="min_price")
    private Integer minPrice;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"icon", "bgColor", "products"})
    private Category category;

    private String name;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Review> reviews;

    @Column(name = "total_rating")
    private Double totalRating;

    @Column(name="sold_quantity")
    private Integer soldQuantity;

    @Column(name="quantity")
    private Integer quantity;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<ProductDetail> productDetails;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    @JsonIgnore
    List<Image> images;

    @OneToOne
    @JoinColumn(name = "default_pd", referencedColumnName = "product_detail_id")
    private ProductDetail productDetail;

    @Transient
    private List<ColorSlugResponse> fetchColor;
    @Transient
    private String img;
}
