package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String addressId;

    @Column(length = 100, name = "address_name")
    private String addressName;

    @Column(length = 50, name = "recipient")
    String recipient;

    @Column(name = "phone", length = 15)
    String phone;

    @Builder.Default
    @Column(name = "is_default")
    boolean isDefault=false;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    User user;

    @OneToMany(mappedBy = "address")
    @JsonIgnore
    private Set<PurchaseOrder> purchaseOrders;
}
