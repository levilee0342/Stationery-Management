package com.project.stationery_be_server.dto.response;

import com.project.stationery_be_server.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    String addressId;
    String addressName;
    String phone;
    String recipient;
    Boolean isDefault;

}
