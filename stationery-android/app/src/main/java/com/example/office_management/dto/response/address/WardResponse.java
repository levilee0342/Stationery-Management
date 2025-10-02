package com.example.office_management.dto.response.address;

public class WardResponse {
    private String _id;
    private String name;
    private String type;
    private String slug;
    private String path_with_type;
    private String name_with_type;
    private String code;
    private String parent_code;

    public WardResponse(){}

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSlug() {
        return slug;
    }

    public String getPath_with_type() {
        return path_with_type;
    }

    public String getName_with_type() {
        return name_with_type;
    }

    public String getCode() {
        return code;
    }

    public String getParent_code() {
        return parent_code;
    }
}
