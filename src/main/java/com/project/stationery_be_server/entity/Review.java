package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class    Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private String reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"email", "phone", "password","dob","role","addresses","carts","blocked","otpCreatedAt","otp"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    private String content;
    private Integer rating;

    @Column(name = "review_image")
    private String reviewImage;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "review_id")
    @JsonBackReference
    private Review parentReview;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Review> replies;

    @ManyToOne
    @JoinColumn(name = "reply_on_user", referencedColumnName = "user_id")
    @JsonIgnoreProperties({"email", "phone", "password","dob","role","addresses","carts","blocked","otpCreatedAt","otp"})
    private User replyOnUser;

    @Column(name = "create_at")
    private Date createdAt;
}
