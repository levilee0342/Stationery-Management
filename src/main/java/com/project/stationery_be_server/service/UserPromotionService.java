package com.project.stationery_be_server.service;

import com.project.stationery_be_server.entity.UserPromotion;

import java.util.List;

public interface UserPromotionService {
    List<UserPromotion> getVouchersForUser();
}
