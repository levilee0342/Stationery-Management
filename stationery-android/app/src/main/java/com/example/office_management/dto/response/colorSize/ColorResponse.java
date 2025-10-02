package com.example.office_management.dto.response.colorSize;

public class ColorResponse {
    private String colorId;
    private String name;
    private String hex;

    public ColorResponse(String colorId, String name, String hex){
        this.colorId = colorId;
        this.name = name;
        this.hex = hex;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }
}
