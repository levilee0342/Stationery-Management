package com.project.stationery_be_server.dto.response;

import com.project.stationery_be_server.entity.Address;
import com.project.stationery_be_server.entity.Cart;
import com.project.stationery_be_server.entity.Role;
import com.project.stationery_be_server.entity.SearchHistory;
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
     Date dob;
     Set<SearchHistory> searchHistory;
}
