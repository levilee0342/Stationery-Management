package com.project.stationery_be_server.mapper;

import com.project.stationery_be_server.dto.response.promotion.ProductDetailPromotion;
import com.project.stationery_be_server.dto.response.promotion.PromotionResponse;
import com.project.stationery_be_server.dto.response.promotion.UserInPromotion;
import com.project.stationery_be_server.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "user", source = "userPromotions")
    @Mapping(target = "pd", source = "productPromotions")
    PromotionResponse toDto(Promotion promotion);

    default List<UserInPromotion> mapUserPromotions(Set<UserPromotion> userPromotions) {
        if (userPromotions == null) return List.of();
        return userPromotions.stream()
                .map(up -> {
                    User user = up.getUser();
                    return UserInPromotion.builder()
                            .userId(user.getUserId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .build();
                }).toList();
    }

    default List<ProductDetailPromotion> mapProductPromotions(Set<ProductPromotion> productPromotions) {
        if (productPromotions == null) return List.of();
        return productPromotions.stream()
                .map(pp -> {
                    ProductDetail detail = pp.getProductDetail();
                    return ProductDetailPromotion.builder()
                            .productDetailId(detail.getProductDetailId())
                            .name(detail.getName())
                            .build();
                }).toList();
    }
}
