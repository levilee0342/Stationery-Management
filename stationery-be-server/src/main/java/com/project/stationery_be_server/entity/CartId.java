package com.project.stationery_be_server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public class CartId {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "product_detail_id")
    private String productDetailId;

}