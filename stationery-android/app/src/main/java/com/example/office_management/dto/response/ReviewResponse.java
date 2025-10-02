package com.example.office_management.dto.response;

import com.example.office_management.model.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReviewResponse implements Serializable {
    private String reviewId;
    private User user;
    private String content;
    private Integer rating;
    //private ReviewResponse parentReview;
    private List<String> reviewImage;
    private List<ReviewResponse> replies;
    private User replyOnUser;
    private Date createdAt;

    public ReviewResponse(){}

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<String> getReviewImage() {
        return reviewImage;
    }

    public void setReviewImage(List<String> reviewImage) {
        this.reviewImage = reviewImage;
    }

    public List<ReviewResponse> getReplies() {
        return replies;
    }

    public void setReplies(List<ReviewResponse> replies) {
        this.replies = replies;
    }

    public User getReplyOnUser() {
        return replyOnUser;
    }

    public void setReplyOnUser(User replyOnUser) {
        this.replyOnUser = replyOnUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
