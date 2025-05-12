package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private String roleId; // Đổi thành camelCase

    @Column(name = "role_name", length = 50)
    private String roleName; // Đổi thành camelCase

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<User> users; // Đổi thành số nhiều để đúng nghĩa
}
