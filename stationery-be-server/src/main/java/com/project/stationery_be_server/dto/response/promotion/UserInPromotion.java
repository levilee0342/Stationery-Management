package com.project.stationery_be_server.dto.response.promotion;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInPromotion {
    String userId;
    String firstName;
    String lastName;
}
