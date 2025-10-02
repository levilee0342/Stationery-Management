package com.example.office_management.dto.response;

public class DetectionResponse {
    private String label;
    private float confidence;

    public String getLabel() {
        return label;
    }

    public float getConfidence() {
        return confidence;
    }
}
