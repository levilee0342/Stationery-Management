package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
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
    @Convert(converter = StringListConverter.class)
    private List<String> reviewImage;

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

// Converter để chuyển đổi List<String> thành chuỗi JSON và ngược lại
@Converter
class StringListConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting List<String> to JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? new ArrayList<>() : objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to List<String>", e);
        }
    }
}
