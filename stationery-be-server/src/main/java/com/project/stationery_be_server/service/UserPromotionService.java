package com.project.stationery_be_server.service;

import com.project.stationery_be_server.entity.UserPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserPromotionService {
    List<UserPromotion> getVouchersForUser();
    Page<UserPromotion> getAllUserPromotionPagination(Pageable pageable, String search);

}
