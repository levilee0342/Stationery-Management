package com.project.stationery_be_server.dto.request;
import com.project.stationery_be_server.entity.Address;
import com.project.stationery_be_server.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    String addressId;
    String addressName;
    String recipient;
    String phone;
    Boolean isDefault;
}
