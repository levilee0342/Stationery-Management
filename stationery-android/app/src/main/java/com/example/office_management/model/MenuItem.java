package com.example.office_management.model;

public class MenuItem {
    private final String title;
    private final int imageResId;

    public MenuItem(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}
