package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.service.UserPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user-promotions")
@RequiredArgsConstructor
public class UserPromotionController {

    private final UserPromotionService userPromotionService;

    @GetMapping()
    public ApiResponse<List<UserPromotion>> getUserPromotions() {
        return ApiResponse.<List<UserPromotion>>builder().result(userPromotionService.getVouchersForUser()).build();
    }
}
