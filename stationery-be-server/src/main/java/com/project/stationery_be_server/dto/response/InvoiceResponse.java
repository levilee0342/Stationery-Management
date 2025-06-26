package com.project.stationery_be_server.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {
    private String purchaseOrderId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String pdfUrl;
}