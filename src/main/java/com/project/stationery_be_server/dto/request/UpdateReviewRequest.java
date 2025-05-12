package com.project.stationery_be_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateReviewRequest {
    private String commentId;
    private String content;
    private Integer rating;
}
