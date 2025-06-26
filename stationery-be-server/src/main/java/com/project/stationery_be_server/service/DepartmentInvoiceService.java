package com.project.stationery_be_server.service;

import com.project.stationery_be_server.dto.response.InvoiceResponse;
import com.project.stationery_be_server.dto.response.momo.MomoResponse;
import com.project.stationery_be_server.dto.response.MonthlyInvoiceSummaryResponse;

import java.util.List;

public interface DepartmentInvoiceService {
    MonthlyInvoiceSummaryResponse getCurrentMonthInvoiceSummary(String userId);
    String generateCurrentInvoicePdf(String userId);
    MomoResponse payCurrentInvoice(String userId);
    List<String> checkOverdueInvoices(String userId);
    List<InvoiceResponse> getAllInvoices(String userId);
}