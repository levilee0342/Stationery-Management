package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.repository.UserPromotionRepository;
import com.project.stationery_be_server.service.UserPromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPromotionServiceImpl implements UserPromotionService {
    final UserPromotionRepository userPromotionRepository;

    @Override
    public List<UserPromotion> getVouchersForUser() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return userPromotionRepository.findUserPromotionForUser(userId);
    }
}
