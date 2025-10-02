package com.example.office_management.dto.request.review;

import java.util.List;

public class ReviewRequest {
    private String productId;
    private String content;
    private String parentId;
    private Integer rating;
    private String replyOnUser;
    private List<String> reviewImage;

    public ReviewRequest(){}

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReplyOnUser() {
        return replyOnUser;
    }

    public void setReplyOnUser(String replyOnUser) {
        this.replyOnUser = replyOnUser;
    }

    public List<String> getReviewImage() {
        return reviewImage;
    }

    public void setReviewImage(List<String> reviewImage) {
        this.reviewImage = reviewImage;
    }
}
