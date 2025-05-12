package com.project.stationery_be_server.dto.request.order;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderProductRequest {
    private String productDetailId;
    private Integer quantity;
    private String productPromotionId;
}
