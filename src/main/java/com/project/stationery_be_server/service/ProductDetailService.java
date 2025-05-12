package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.DeleteProductDetailRequest;
import org.springframework.stereotype.Service;

public interface ProductDetailService {
    void deleteProductDetail(DeleteProductDetailRequest request);
}
