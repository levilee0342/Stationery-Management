package com.project.stationery_be_server.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyInvoiceSummaryResponse {
    private String userId;
    private int month;
    private int year;
    private BigDecimal totalAmount;
    private int orderCount;
}
