package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.DeletePromotionRequest;
import com.project.stationery_be_server.entity.Promotion;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.InvalidatedTokenRepository;
import com.project.stationery_be_server.repository.PromotionRepository;
import com.project.stationery_be_server.repository.UserRepository;
import com.project.stationery_be_server.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionServiceImpl implements PromotionService {
    final PromotionRepository promotionRepository;
    final UserRepository userRepository;

    @Override
    public BigDecimal applyPromotion(String promoCode, BigDecimal orderTotal, User user) {
        return null;
    }

    @Override
    public List<Promotion> getAvailablePromotions(User user, BigDecimal orderTotal) {
        return List.of();
    }

    @Override
    public BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderTotal) {
        return null;
    }

    @Override
    @Transactional
    public void deletePromotion(DeletePromotionRequest request){
        var context = SecurityContextHolder.getContext();
        String userIdLogin = context.getAuthentication().getName();
        User user = userRepository.findById(userIdLogin)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        // admin moi dc xoa
        if (!user.getRole().getRoleName().equals("admin")){
            throw new RuntimeException("You do not have permission to delete users");
        }
        String promotionId = request.getPromotionId();
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(()-> new RuntimeException("Promotion not found")) ;

        // Dem so luong su dung cua promotion
        int userUsage = promotionRepository.countUserPromotionUsage(promotionId);
        int productUsage = promotionRepository.countProductPromotionUsage(promotionId);
        if(userUsage > 0) {
            throw new RuntimeException("Cannot delete this promotion because it is currently being used by users.");
        }
        if(productUsage > 0) {
            throw new RuntimeException("Cannot delete this promotion because it is currently being used by products.");
        }
        promotionRepository.delete(promotion);

    }
}
