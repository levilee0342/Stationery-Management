package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.DeletePromotionRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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


}