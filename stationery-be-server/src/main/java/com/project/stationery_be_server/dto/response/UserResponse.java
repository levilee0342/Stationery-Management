package com.project.stationery_be_server.dto.response;

import com.project.stationery_be_server.entity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
     String userId;
     String avatar;
     String firstName;
     String lastName;
     String email;
     String phone;
     Set<Address> addresses; // Foreign key to Address
     Role role;
     Set<Cart> carts;
     List<InOrder> inOrders; // Foreign key to InOrder
     Boolean block;
     Date dob;
     Set<SearchHistory> searchHistory;
}
