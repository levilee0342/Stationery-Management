package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.DeleteProductDetailRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.service.ProductDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-details")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductDetailController {
    ProductDetailService productDetailService;

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteProductDetail(@RequestBody DeleteProductDetailRequest request){
        productDetailService.deleteProductDetail(request);
        return ApiResponse
                .<String>builder()
                .result("Product details deleted successfully")
                .build();
    }
}
