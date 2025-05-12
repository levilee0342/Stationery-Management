package com.project.stationery_be_server.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ColorSizeSlugResponse {
    private String colorId;
    private String hex;
    @JsonIgnore
    private String sizes;

    public ColorSizeSlugResponse(String colorId, String hex , String sizes) {
        this.colorId = colorId;
        this.hex = hex;
        this.sizes = sizes;
    }

    @JsonProperty("sizes")
    public List<SizeSlugResponse> getParsedSizes() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(sizes, new TypeReference<List<SizeSlugResponse>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}


