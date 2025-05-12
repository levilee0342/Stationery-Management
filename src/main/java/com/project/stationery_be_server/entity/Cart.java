package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Cart {
    @EmbeddedId
    CartId cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")  // Liên kết với userId trong CartId
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-carts")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productDetailId")  // Liên kết với productDetailId trong CartId
    @JoinColumn(name = "product_detail_id", nullable = false)
    @JsonBackReference
    private ProductDetail productDetail;

    int quantity;
    private Date createdAt;
}
