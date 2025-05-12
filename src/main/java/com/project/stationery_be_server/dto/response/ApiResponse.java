package com.project.stationery_be_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Những filed nào null thì nó k trả về
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    //       Đặt giá trị default
    @Builder.Default
    int code = 200;
    String message;
    T result;
}
