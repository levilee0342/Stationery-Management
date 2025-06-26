package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.dto.request.ReviewRequest;
import com.project.stationery_be_server.dto.request.UpdateReviewRequest;
import com.project.stationery_be_server.entity.Review;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.repository.ProductDetailRepository;
import com.project.stationery_be_server.repository.ReviewRepository;
import com.project.stationery_be_server.service.ProductService;
import com.project.stationery_be_server.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {
    ReviewRepository reviewRepository;
    ProductService productService;
    ProductDetailRepository productDetailRepository;

    @Override
    @Transactional
    public List<Review> getReviewByProductId(String slug) {
        String productId = productDetailRepository.findProductIdBySlug(slug);
        return reviewRepository.findByProduct_ProductIdAndParentReviewIsNull(productId);
    }

    @Override
    @Transactional
    public void creatReview(ReviewRequest request) {

        Integer rating = null;
        if (request.getRating() != null) {
            rating = request.getRating();
        }
        String productId = request.getProductId();

        String parentId = request.getParentId();
        String replyOnUser = null;
        if (parentId != null) {
            Review rv = reviewRepository.findById(parentId)
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.COMMENT_NOT_FOUND));
            productId = rv.getProduct().getProductId();
            // cha comment
            String parentUserId = rv.getUser().getUserId();
            replyOnUser = request.getReplyOnUser();
            if (replyOnUser == null || replyOnUser.isEmpty()) {
                throw new IllegalArgumentException("Reply on user cannot be empty");
            }
            if(!reviewRepository.existsByUser_UserIdAndParentReview_ReviewId(replyOnUser, parentId) && !parentUserId.equals(replyOnUser)  ){
                throw new IllegalArgumentException("Reply on user does not exist in this repling");
            }
        }
        String content = request.getContent();
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        if (rating != null && parentId != null) {
            throw new IllegalArgumentException("Rating and parent id cannot be present at the same time");
        }
        if (rating != null) {
            if (rating > 5 || rating < 0) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }
            var isRated = reviewRepository.findByProductProductIdAndUserUserIdAndRatingIsNotNull(productId, userId);
            if (isRated.isPresent()) {
                throw new IllegalArgumentException("You have already rated this product");
            }
            // khắc phục lỗi k kích hoạt đc transaction
            productService.handleUpdateTotalProductRating(productId, "create", rating);
        }
        if (rating != null || parentId != null) {
            reviewRepository.createReview(productId, userId, content, rating, parentId, replyOnUser);
        } else {
            throw new IllegalArgumentException("This review is not include rating");
        }

    }

    @Override
    @Transactional
    public void updateReview(UpdateReviewRequest request) {
        try {
            String commentId = request.getCommentId();
            if (commentId.isEmpty()) {
                throw new IllegalArgumentException("Comment ID cannot be empty");
            }

            Review comment = reviewRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.COMMENT_NOT_FOUND));
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!comment.getUser().getUserId().equals(userId)) {
                throw new RuntimeException("You are not the owner of this comment");
            }
            if (request.getRating() != null && comment.getParentReview() != null) {
                throw new RuntimeException("This comment does not have rating, please remove rating");
            }
            Integer newRating = comment.getRating();
            if (request.getRating() != null) {
                newRating = request.getRating();
                if (newRating < 0 || newRating > 5) {
                    throw new RuntimeException("Rating must be between 0 and 5");
                }

                // Cập nhật rating tổng
                productService.handleUpdateTotalProductRating(comment.getProduct().getProductId(), "UPDATE", newRating - comment.getRating());
            }
            comment.setRating(newRating);
            if (!request.getContent().isEmpty()) {
                comment.setContent(request.getContent());
            }
            reviewRepository.save(comment);

        } catch (Exception e) {
            throw new RuntimeException("Error updating review: " + e.getMessage());
        }
    }

    @Override
    public void deleteReview(String reviewId) {
        try {
            Review comment = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new AppException(NotExistedErrorCode.COMMENT_NOT_FOUND));
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!comment.getUser().getUserId().equals(userId)) {
                throw new RuntimeException("You are not the owner of this comment");
            }

            // Cập nhật tổng đánh giá nếu có
            if (comment.getRating() != null) {
                productService.handleUpdateTotalProductRating(
                        comment.getProduct().getProductId(),
                        "DELETE",
                        -comment.getRating()
                );
            }

            // Xóa comment chính
            reviewRepository.deleteById(comment.getReviewId());

            // Xóa các comment con
            reviewRepository.deleteReviewByParentId(reviewId);

        } catch (Exception e) {
            throw new RuntimeException("Error deleting review: " + e.getMessage());
        }
    }


}
