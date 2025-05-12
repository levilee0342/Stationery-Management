package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.ProductPromotion;
import com.project.stationery_be_server.entity.Promotion;
import com.project.stationery_be_server.entity.User;
import com.project.stationery_be_server.entity.UserPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPromotionRepository extends JpaRepository<UserPromotion, String> {

    @Query(value = """
    SELECT up.* FROM user_promotion up
    JOIN promotion p ON up.promotion_id = p.promotion_id
    WHERE
      p.start_date <= NOW()
      AND p.end_date >= NOW()
      AND up.user_id = :userId
      AND (p.usage_limit IS NULL OR p.usage_limit > 0)
    """, nativeQuery = true)
    List<UserPromotion> findUserPromotionForUser(String userId);
    @Query(value = """
    SELECT up.* FROM user_promotion up
    JOIN promotion p ON up.promotion_id = p.promotion_id
    WHERE up.user_promotion_id = :userPromotionId
      AND p.start_date <= NOW()
      AND p.end_date >= NOW()
      AND p.min_order_value <= :price
      AND (p.usage_limit IS NULL OR p.usage_limit > 0)
    """, nativeQuery = true)
    Optional<UserPromotion> getValidPromotionForUser(
            @Param("userPromotionId") String userPromotionId,
            @Param("price") Long price
    );


}
