package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.DeleteProductDetailRequest;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.dto.response.promotion.ProductDetailPromotion;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.util.List;

public interface ProductDetailService {
    void deleteProductDetail(DeleteProductDetailRequest request);
    ProductDetailResponse updateProductDetail(String pd, List<String> imageIndexes, List<MultipartFile> images);
    Boolean updateHiddenPD(String productDetailId, boolean isHidden);
    List<ProductDetailPromotion> getAllProductDetailSimple();
}
