package com.example.office_management.dto.response;

import com.example.office_management.dto.response.product.ProductResponse;

import java.util.List;

public class ResultResponse {
    private List<ProductResponse> content;

    public List<ProductResponse> getContent() { return content; }
}
