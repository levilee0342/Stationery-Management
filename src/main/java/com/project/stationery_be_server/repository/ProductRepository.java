package com.project.stationery_be_server.repository;


import com.project.stationery_be_server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface ProductRepository extends JpaRepository<Product, String> , JpaSpecificationExecutor<Product> {
    Product findBySlug(String slug);
    long countByProductDetail_ProductDetailId(String productDetailId);
}
