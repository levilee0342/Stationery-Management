package com.project.stationery_be_server.dto.response;

import com.project.stationery_be_server.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {
    String userId ;
    String avatar;
    String firstName;
    String lastName;
    String email;
    String phone;
    Role role;
    Date dob;
    Boolean block;
}
