package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, String> {
    @Query(value = """
            SELECT pp.* from product_promotion pp
            JOIN promotion p ON pp.promotion_id = p.promotion_id
            where pp.product_detail_id = :productDetailId
            and p.start_date <= NOW()
            and p.end_date >= NOW()
            and p.min_order_value <= :price
            AND (p.usage_limit IS NULL OR p.usage_limit > 0)
            LIMIT 1
            """, nativeQuery = true)
    List<ProductPromotion> findValidPromotionForProductDetail(String productDetailId, String price);

    @Query(value = """
    SELECT pp.* FROM product_promotion pp
    JOIN promotion p ON pp.promotion_id = p.promotion_id
    WHERE pp.product_promotion_id = :productPromotionId
      AND p.start_date <= NOW()
      AND p.end_date >= NOW()
      AND p.min_order_value <= :price
      AND (p.usage_limit IS NULL OR p.usage_limit > 0)
    """, nativeQuery = true)
    Optional<ProductPromotion> getValidPromotionForProductDetail(
            @Param("productPromotionId") String productPromotionId,
            @Param("price") Integer price
    );
    long countByProductDetail_ProductDetailId(String productDetailId);
}
