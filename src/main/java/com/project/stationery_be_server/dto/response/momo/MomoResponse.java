package com.project.stationery_be_server.dto.response.momo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoResponse {
    String partnerCode;
    String requestId;
    String orderId;
    Long amount;
    Long responseTime; // timestamp
    String message;
    Integer resultCode;
    String payUrl;
    String deeplink;
    String qrCodeUrl;
    String deeplinkMiniApp;
    String signature;
}
