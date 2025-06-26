package com.project.stationery_be_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColorResponse {
    String colorId;
    String name;
    String hex;
}
