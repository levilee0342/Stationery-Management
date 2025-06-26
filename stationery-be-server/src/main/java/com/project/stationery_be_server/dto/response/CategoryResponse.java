package com.project.stationery_be_server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    private String categoryId;
    private String categoryName;
    private String icon;
    private String bgColor;
}