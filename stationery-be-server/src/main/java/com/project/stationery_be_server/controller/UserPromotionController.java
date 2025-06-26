package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.entity.ProductPromotion;
import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.service.PromotionService;
import com.project.stationery_be_server.service.UserPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user-promotions")
@RequiredArgsConstructor
public class UserPromotionController {

    private final UserPromotionService userPromotionService;
    private final PromotionService promotionService;

    @GetMapping()
    public ApiResponse<List<UserPromotion>> getUserPromotions() {
        return ApiResponse.<List<UserPromotion>>builder().result(userPromotionService.getVouchersForUser()).build();
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/admin/get-user-promotions")
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
