package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.request.ReviewRequest;
import com.project.stationery_be_server.dto.request.UpdateReviewRequest;
import com.project.stationery_be_server.entity.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getReviewByProductId(String slug);
    void creatReview(ReviewRequest request);
    void updateReview(UpdateReviewRequest request);
    void deleteReview(String reviewId);
}
