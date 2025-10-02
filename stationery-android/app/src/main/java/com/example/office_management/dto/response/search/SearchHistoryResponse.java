package com.example.office_management.dto.response.search;

import java.sql.Date;
import java.time.LocalDateTime;

public class SearchHistoryResponse {
    private String searchId;

    private String keyword;

    private String createdAt;

    public SearchHistoryResponse() {}

    public String getSearchId() {
        return searchId;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

