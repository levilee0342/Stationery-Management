package com.project.stationery_be_server.dto.request;

import com.project.stationery_be_server.entity.Address;
import com.project.stationery_be_server.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String firstName;
    String lastName;
    String email;
    String phone;
    Date dob;
    String avatar;
}
