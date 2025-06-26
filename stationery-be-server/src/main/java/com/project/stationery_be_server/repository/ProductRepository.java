package com.project.stationery_be_server.repository;


import com.project.stationery_be_server.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, String> , JpaSpecificationExecutor<Product> {
    Product findBySlug(String slug);
    long countByProductDetail_ProductDetailId(String productDetailId);
    Page<Product> findAllByOrderBySoldQuantityDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.categoryId = :categoryId AND p.productId != :productId ORDER BY p.createdAt DESC")
    List<Product> findTopSimilarProducts(@Param("categoryId") String categoryId, @Param("productId") String productId, Pageable pageable);

    Boolean existsByName(String name);

}
