package com.example.office_management.dto.response.colorSize;

public class ColorSlugResponse {
    private String colorId;
    private String hex;
    private String slug;

    public ColorSlugResponse(String colorId, String hex, String slug) {
        this.colorId = colorId;
        this.hex = hex;
        this.slug = slug;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}