package com.project.stationery_be_server.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderRequest {
    private List<PurchaseOrderProductRequest> orderDetails;
    private String userPromotionId;
    private String recipient;
    private String addressId;
}