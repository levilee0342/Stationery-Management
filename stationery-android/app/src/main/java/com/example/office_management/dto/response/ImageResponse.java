package com.example.office_management.dto.response;

public class ImageResponse {
    private String imageId;
    private String url;
    private Integer priority;

    public ImageResponse(String imageId, String url, Integer priority) {
        this.imageId = imageId;
        this.url = url;
        this.priority = priority;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public Integer getPriority() {
        return priority;
    }

}
