package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id") // Giữ nguyên snake_case trong DB
    String userId;

    @Column(name = "first_name", length = 50)
    String firstName;

    @Column(name = "last_name", length = 50)
    String lastName;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "phone", length = 15)
    String phone;

    @Column(name = "password", length = 100)
    String password;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    Date dob;

    @Column(name = "avatar", length = 255)
    String avatar;

    @Column(name = "is_blocked") // Chuyển isBlock -> isBlocked
    boolean isBlocked;

    @Column(name = "otp")
    Integer otp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "otp_created_at")
    Date otpCreatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Review> replyOnUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference("user-addresses") // Đặt tên riêng
    Set<Address> addresses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference("user-carts") // Đặt tên riêng
    Set<Cart> carts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<PurchaseOrder> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference("user-searchHistories")
    Set<SearchHistory> searchHistories;
}
