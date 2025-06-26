package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @Column(name = "category_id", length = 30)
    private String categoryId;

    @Column(name = "category_name", length = 30, nullable = false, unique = true)
    private String categoryName;

    @Column(name = "icon", length = 10, nullable = false)
    private String icon;

    @Column(name = "bg_color", length = 20, nullable = false)
    private String bgColor;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Product> products;
}
