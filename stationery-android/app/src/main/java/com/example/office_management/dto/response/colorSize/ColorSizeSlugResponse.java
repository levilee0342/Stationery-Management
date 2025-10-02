package com.example.office_management.dto.response.colorSize;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ColorSizeSlugResponse {
    @SerializedName("colorId")
    private String colorId;

    @SerializedName("hex")
    private String hex;

    @SerializedName("sizes")
    private List<SizeSlugResponse> sizes;

    public String getColorId() {
        return colorId;
    }

    public String getHex() {
        return hex;
    }

    public List<SizeSlugResponse> getSizes() {
        return sizes;
    }

    @Override
    public String toString() {
        return "ColorSizeSlugResponse{" +
                "colorId='" + colorId + '\'' +
                ", hex='" + hex + '\'' +
                ", sizes=" + sizes +
                '}';
    }
}