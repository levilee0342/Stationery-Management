package com.project.stationery_be_server.controller;


import com.project.stationery_be_server.dto.request.ReviewRequest;
import com.project.stationery_be_server.dto.request.UpdateReviewRequest;
import com.project.stationery_be_server.dto.response.ApiResponse;
import com.project.stationery_be_server.entity.Review;
import com.project.stationery_be_server.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;
    @GetMapping("/{slug}")
    public ApiResponse<List<Review> > getReviewByProductId(@PathVariable String slug) {
        List<Review> review = reviewService.getReviewByProductId(slug);
        return ApiResponse.<List<Review> >builder()
                .message("Get review successfully")
                .result(review)
                .build();
    }
    @PostMapping
    public ApiResponse<Void> createReview(@RequestBody ReviewRequest reviewRequest) {
        reviewService.creatReview(reviewRequest);
        return ApiResponse.<Void>builder()
                .message("Review created successfully")
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<ApiResponse<Void>> updateReview(@PathVariable String id, @RequestBody UpdateReviewRequest request) {
        request.setCommentId(id);
        reviewService.updateReview(request);
        return ApiResponse.<ApiResponse<Void>>builder()
                .message("Review updated successfully")
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ApiResponse.<Void>builder()
                .message("Review deleted successfully")
                .build();
    }
}
