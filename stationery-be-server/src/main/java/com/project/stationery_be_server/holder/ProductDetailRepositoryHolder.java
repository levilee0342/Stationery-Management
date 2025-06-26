package com.project.stationery_be_server.holder;

import com.project.stationery_be_server.repository.ProductDetailRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailRepositoryHolder {
    public static ProductDetailRepository productDetailRepository;

    public ProductDetailRepositoryHolder(ProductDetailRepository repository) {
        ProductDetailRepositoryHolder.productDetailRepository = repository;
    }
}