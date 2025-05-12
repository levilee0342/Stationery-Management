package com.project.stationery_be_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SizeResponse {
    String sizeId;
    String name;
    Integer priority;
}
