package com.example.office_management.dto.response.address;

public class ProvinceResponse {
    private String _id;
    private String name;
    private String slug;
    private String type;
    private String code;

    public ProvinceResponse(){}

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

}
