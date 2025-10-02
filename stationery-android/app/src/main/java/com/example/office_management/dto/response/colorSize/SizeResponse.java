package com.example.office_management.dto.response.colorSize;

public class SizeResponse {
    private String sizeId;
    private String name;
    private Integer priority;

    public SizeResponse(String sizeId, String name, Integer priority){
        this.sizeId = sizeId;
        this.name = name;
        this.priority = priority;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
