package com.project.stationery_be_server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoRequest {
    String partnerCode;
    String requestId;
    Long amount;
    String orderId;
    String orderInfo;
    String redirectUrl;
    String ipnUrl;
    String requestType;
    String extraData;
    String lang;
    String signature;
}
