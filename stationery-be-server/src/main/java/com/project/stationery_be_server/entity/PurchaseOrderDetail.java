package com.project.stationery_be_server.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PurchaseOrderDetail {
    @EmbeddedId
    private PurchaseOrderDetailId purchaseOrderDetailId;

    @ManyToOne
    @MapsId("purchaseOrderId")  // Trùng với tên trường trong PurchaseOrderDetailId
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @MapsId("productDetailId")  // Trùng với tên trường trong PurchaseOrderDetailId
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail;

    private Integer originalPrice;
    private Integer discountPrice;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_promotion_id")
    private ProductPromotion productPromotion;

}
