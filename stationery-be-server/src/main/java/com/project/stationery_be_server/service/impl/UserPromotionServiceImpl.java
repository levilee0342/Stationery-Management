package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.entity.UserPromotion;
import com.project.stationery_be_server.repository.UserPromotionRepository;
import com.project.stationery_be_server.service.NotificationService;
import com.project.stationery_be_server.service.UserPromotionService;
import com.project.stationery_be_server.specification.UserPromotionSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    @Override
    public Page<UserPromotion> getAllUserPromotionPagination(Pageable pageable, String search) {
        Specification<UserPromotion> spec = UserPromotionSpecification.filterUserPromotion(search);
        Page<UserPromotion> userPromotionPage = userPromotionRepository.findAll(spec, pageable);
        // Có thể thêm logic tính toán thêm ở đây
        List<UserPromotion> userResponses = userPromotionPage.getContent().stream()
                .toList();
        return new PageImpl<>(userResponses, pageable, userPromotionPage.getTotalElements());
    }
}
