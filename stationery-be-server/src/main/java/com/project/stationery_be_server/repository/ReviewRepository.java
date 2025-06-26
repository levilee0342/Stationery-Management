package com.project.stationery_be_server.repository;

import com.project.stationery_be_server.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<com.project.stationery_be_server.entity.Review, String> {
  Optional<Review> findByProductProductIdAndUserUserIdAndRatingIsNotNull(String productId, String userId);

  // Đếm số lượng review có rating
  @Query("SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId AND r.rating IS NOT NULL")
  int countByProductId(String productId);

  // Tính tổng số sao của tất cả các review có rating
  @Query("SELECT COALESCE(SUM(r.rating), 0) FROM Review r WHERE r.product.productId = :productId AND r.rating IS NOT NULL")
  int sumRatingByProductId(String productId);

  @Modifying // hiểu là truy vấn thay đổi dữ liệu
  @Transactional
  @Query(value = "INSERT INTO review (review_id, product_id, user_id, content, rating, parent_id, reply_on_user, create_at) " +
          "VALUES (UUID(), :productId, :userId, :content, :rating, :parentId,:replyOnUser, NOW())", nativeQuery = true)
  void createReview(@Param("productId") String productId,
                    @Param("userId") String userId,
                    @Param("content") String content,
                    @Param("rating") Integer rating,
                    @Param("parentId") String parentId,
                    @Param("replyOnUser") String replyOnUser);

  @Modifying
  @Transactional
  @Query("DELETE from Review r where r.reviewId = :parentId")
  void deleteReviewByParentId(@Param("parentId") String parentId);

  boolean existsByUser_UserIdAndParentReview_ReviewId(String replyOnUser, String parentId);

  List<Review> findByProduct_ProductIdAndParentReviewIsNull(String productId);
  long countByProduct_ProductId(String productId);
}