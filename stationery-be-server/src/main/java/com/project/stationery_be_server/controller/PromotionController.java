package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.DeletePromotionRequest;
import com.project.stationery_be_server.dto.request.PromotionRequest;
import com.project.stationery_be_server.dto.request.UpdatePromotionRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.dto.response.promotion.PromotionResponse;
import com.project.stationery_be_server.entity.ProductPromotion;
import com.project.stationery_be_server.entity.Promotion;
import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.dto.response.ColorResponse;
import com.project.stationery_be_server.entity.ProductPromotion;
import com.project.stationery_be_server.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {
    final PromotionService promotionService;

    @DeleteMapping
    public ApiResponse<String> deletePromotion(@RequestBody DeletePromotionRequest request){
        promotionService.deletePromotion(request);
        return ApiResponse.<String>builder()
                .result("Promotion deleted successfully")
                .build();
    }

    @PostMapping()
    public ApiResponse<String> createPromotion(@RequestBody PromotionRequest request) {
        promotionService.createPromotion(request);
        return ApiResponse.<String>builder()
                .result("Promotion created successfully" )
                .build();
    }
    @PutMapping("/update")
    public ApiResponse<String> updatePromotion(@RequestBody UpdatePromotionRequest request) {
        promotionService.updatePromotion(request);
        return ApiResponse.<String>builder()
                .result("Promotion updated successfully")
                .build();
    }
    @GetMapping("/my-vouchers")
    public ApiResponse<Page<Promotion>> getMyVouchers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        // Chuyển page 1-based thành 0-based
        if (page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Promotion> pageResult = promotionService.getMyVouchers(pageable);
        return ApiResponse.<Page<Promotion>>builder()
                .result(pageResult)
                .build();
    }
    @GetMapping("/all-user-vouchers")
    public ApiResponse<Page<UserPromotion>> getAllUserVouchers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        if (page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<UserPromotion> pageUP = promotionService.getAllUserVouchers(pageable);
        return ApiResponse.<Page<UserPromotion>>builder()
                .result(pageUP)
                .build();
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/all-promotions")
    public ApiResponse<Page<PromotionResponse>> getAllProductPromotions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search

    ) {
        page = page <= 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, limit);
        Page<PromotionResponse> pagePP = promotionService.getAllPromotion(pageable, search);
        return ApiResponse.<Page<PromotionResponse>>builder()
                .result(pagePP)
                .build();
    }
    @GetMapping("/{promotionId}")
    public ApiResponse<Promotion> getPromotion(@PathVariable String promotionId) {
        Promotion promo = promotionService.getPromotionById(promotionId);
        return ApiResponse.<Promotion>builder()
                .result(promo)
                .build();
    }
    @GetMapping("/user/{userId}")
    public ApiResponse<Page<Promotion>> getPromotionsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        if (page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Promotion> pageResult = promotionService.getPromotionsByUser(userId, pageable);
        return ApiResponse.<Page<Promotion>>builder()
                .result(pageResult)
                .build();
    }
    @GetMapping("/product/{productId}/page")
    public ApiResponse<Page<Promotion>> getPromotionsByProduct(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        if (page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Promotion> pageResult = promotionService.getPromotionsByProduct(productId, pageable);
        return ApiResponse.<Page<Promotion>>builder()
                .result(pageResult)
                .build();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/get-product-promotions")
    public ApiResponse<Page<ProductPromotion>> getAllProductsForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int limit,
                                                                   @RequestParam(required = false) String search

    ) {
        // sử lý ở FE page 1 là BE page 0, page 2 là page 1, ..
        page = page <= 1 ? 0 : page - 1;
        Pageable pageable;

        pageable = PageRequest.of(page, limit);
        Page<ProductPromotion> pageResult = promotionService.getAllProductPromotionPagination(pageable, search);
        return ApiResponse.<Page<ProductPromotion>>builder()
                .result(pageResult)
                .build();
    }

}