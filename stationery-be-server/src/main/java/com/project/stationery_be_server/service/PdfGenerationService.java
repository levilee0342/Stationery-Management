package com.project.stationery_be_server.service;

import com.project.stationery_be_server.entity.PurchaseOrder;

import java.time.LocalDateTime;

public interface PdfGenerationService {
    String generateAndUploadInvoicePdf(PurchaseOrder purchaseOrder);
    String generateAndUploadCurrentInvoicePdf(String userId, LocalDateTime startDate, LocalDateTime endDate);
}