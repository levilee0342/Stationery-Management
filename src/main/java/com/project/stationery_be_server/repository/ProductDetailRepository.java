package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.dto.response.ColorSizeSlugResponse;
import com.project.stationery_be_server.dto.response.ColorSlugResponse;
import com.project.stationery_be_server.entity.Product;
import com.project.stationery_be_server.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, String>, JpaSpecificationExecutor<ProductDetail> {
    // ✅ Truy vấn mới hỗ trợ nullable colorId & sizeId
    @Query("SELECT pd FROM ProductDetail pd " +
           "WHERE pd.product.productId = :productId " +
           "AND (:colorId IS NULL OR pd.color.colorId = :colorId) " +
           "AND (:sizeId IS NULL OR pd.size.sizeId = :sizeId)")
    Optional<ProductDetail> findByProductIdAndOptionalColorIdAndOptionalSizeId(
            @Param("productId") String productId,
            @Param("colorId") String colorId,
            @Param("sizeId") String sizeId);

    @Query("SELECT p.product.productId FROM ProductDetail p WHERE p.slug = :slug")
    String findProductIdBySlug(@Param("slug") String slug);

    ProductDetail findBySlug(String slug);

    ProductDetail findByProductDetailId(String productDetailId);
    List<ProductDetail> findByProduct_ProductId(String productId);
    @Query(value = """
            SELECT
                pd_color.color_id AS colorId,
                pd_color.hex AS hex,
                JSON_ARRAYAGG(
                    IF(
                        pd_color.size_id IS NOT NULL,
                        JSON_OBJECT('slug', pd_color.slug, 'size', pd_color.size_id),
                        JSON_OBJECT('slug', pd_color.slug)
                    )
                ) AS sizes
            FROM (
                SELECT
                    pd.slug,
                    pd.size_id,
                    pd.color_id,
                    c.hex,
                    pd.product_id
                FROM product_detail pd
                JOIN color c ON pd.color_id = c.color_id
                WHERE pd.product_id = (
                    SELECT pd2.product_id
                    FROM product_detail pd2
                    WHERE pd2.slug = :slug
                    LIMIT 1
                )
            ) AS pd_color
            left JOIN size s ON pd_color.size_id = s.size_id
            GROUP BY pd_color.color_id, pd_color.hex
            """, nativeQuery = true)
    List<ColorSizeSlugResponse> fetchColorSizeBySLug(String slug);

    @Query(value = """
            SELECT
                pd.color_id AS colorId,
                c.hex AS hex,
                MIN(pd.slug) AS slug  -- chọn slug bất kỳ, ở đây là nhỏ nhất theo bảng chữ cái
            FROM
                product_detail pd
            JOIN
                color c ON pd.color_id = c.color_id
            WHERE
                pd.product_id = :productId
            GROUP BY
                pd.color_id, c.hex
            """, nativeQuery = true)
    List<ColorSlugResponse> findDistinctColorsWithAnySlug(String productId);


    @Modifying
    @Query("UPDATE ProductDetail pd " +
           "SET pd.stockQuantity = pd.stockQuantity - :amount , pd.soldQuantity = pd.soldQuantity + :amount   " +
           "WHERE pd.productDetailId = :productDetailId AND pd.stockQuantity >= :amount")
    int reduceQuantity(@Param("productDetailId") String productDetailId, @Param("amount") int amount);

    @Query("SELECT pd FROM ProductDetail pd WHERE pd.name LIKE %:keyword% OR pd.slug LIKE %:keyword%")
    List<ProductDetail> findByKeyword(String keyword);
    long countByProduct_ProductId(String productId);
}
