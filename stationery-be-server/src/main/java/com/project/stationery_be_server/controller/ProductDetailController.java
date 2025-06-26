package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.DeleteProductDetailRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.product.ProductDetailResponse;
import com.project.stationery_be_server.dto.response.promotion.ProductDetailPromotion;
import com.project.stationery_be_server.service.ProductDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    @PutMapping("/update/product-detail")
    public ApiResponse<ProductDetailResponse> updateProductDetail(
            @RequestParam("documents") String pd,
            @RequestParam(value = "imgIdToUpdate", required = false) List<String> imgIdToUpdate,
            @RequestParam(value ="images", required = false) List<MultipartFile> images
    ) {
        var response = productDetailService.updateProductDetail(pd, imgIdToUpdate, images);
        return ApiResponse
                .<ProductDetailResponse>builder()
                .message("Product detail updated successfully")
                .result(response)
                .build();
    }
    @PutMapping("/update-hidden-pd/{pdId}")
    public ApiResponse<Boolean> updateHiddenProductDetail(
            @PathVariable String pdId,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean isHidden = body.get("hidden");
        return ApiResponse
                .<Boolean>builder()
                .message("Product detail visibility updated successfully")
                .result(productDetailService.updateHiddenPD(pdId, isHidden))
                .build();
    }

    @GetMapping("/simple")
    public ApiResponse<List<ProductDetailPromotion>> getAllSimpleDetails() {
        return ApiResponse
                .<List<ProductDetailPromotion>>builder()
                .message("Product detail visibility updated successfully")
                .result(productDetailService.getAllProductDetailSimple())
                .build();

    }
}
