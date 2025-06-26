package com.project.stationery_be_server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Color {
    @Id
    @Column(name = "color_id", length = 10)
    String colorId;

    @Column(name = "name", nullable = false, length = 10, unique = true)
    String name;

    @Column(name = "hex", nullable = false, length = 7)
    String hex;

    @OneToMany(mappedBy = "color",fetch = FetchType.LAZY)
    @JsonIgnore
    List<Image> images;
}
