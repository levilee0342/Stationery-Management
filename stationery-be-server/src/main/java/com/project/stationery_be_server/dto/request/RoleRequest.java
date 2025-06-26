package com.project.stationery_be_server.dto.request;

import com.project.stationery_be_server.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    String roleId;
    String roleName;
    String description;
}

